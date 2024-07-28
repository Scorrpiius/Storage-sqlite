package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class tableauBesoinController {
    private final String idCommande;
    private DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> besoinsTable;
    private List<Map<String, Object>> mps, listeProduits;
    private List<Map<String, Object>> tableauBesoins;
    @FXML
    private TableColumn<Map<String, Object>, String> refProduitCol, qteProduitCol;
    @FXML
    private ComboBox<String> referenceFicheFiltre, categorieFicheFiltre, designationFicheFiltre;

    public  tableauBesoinController(DependencyManager dependencyManager, String idCommande){
        this.dependencyManager = dependencyManager;
        this.idCommande = idCommande;
        this.listeProduits = dependencyManager.getCommandeProduitFiniRepository().findByCommandeId(idCommande);
        this.mps = new ArrayList<>();
        this.tableauBesoins = new ArrayList<>();
        /* Création du tableau de besoin */

    }
    public void initialize(){

        updateCategorie();
        updateDesignation();
        updateReference();

        for (Map<String, Object> p : listeProduits) {
            int quantiteProduit = Integer.parseInt(p.get("quantite").toString());
            String idProduit = p.get("id_ProduitFini").toString();

            List<Map<String, Object>> listeMPs = dependencyManager.getProduitFiniMatierePremiereRepository().findByProduitId(idProduit);
            List<Map<String, Object>> ligneTableauBesoin = new ArrayList<>();

            Map<String, Object> ligne = new HashMap<>();
            ligne.put("idProduit", idProduit);
            ligne.put("quantiteProduit", quantiteProduit);

            for (Map<String, Object> mp : listeMPs) {
                int temp = Integer.parseInt(mp.get("quantite").toString());
                ligne.put(mp.get("matiere_id").toString(), temp * quantiteProduit );
                System.out.println(mp.get("matiere_id").toString());
                Map<String, Object> temp_map = new HashMap<>();
                //System.out.println(dependencyManager.getEntreeRepository().getCategorieDesignation(mp.get("matiere_id").toString()));
                this.mps.add(dependencyManager.getEntreeRepository().getCategorieDesignation(mp.get("matiere_id").toString()));
            }
            //ligneTableauBesoin.add(ligne);
            tableauBesoins.add(ligne);
        }
        System.out.println(tableauBesoins);
        System.out.println(mps);
        updateTableauBesoins(null, null, null);

    }

    public void updateCategorie(){
        List<Map<String, Object>> categories = dependencyManager.getCategorieRepository().findAll();
        List<String> categoriesList = new ArrayList<>();

        for(Map<String, Object> c : categories){
            categoriesList.add((String)c.get("nom"));
        }
        categorieFicheFiltre.setItems(FXCollections.observableArrayList(categoriesList));
        categorieFicheFiltre.setEditable(true);
    }
    public void updateDesignation(){
        List<Map<String, Object>> designations = dependencyManager.getDesignationRepository().findAll();
        List<String> designationsList = new ArrayList<>();

        for(Map<String, Object> c : designations){
            designationsList.add((String)c.get("nom"));
        }
        designationFicheFiltre.setItems(FXCollections.observableArrayList(designationsList));
        designationFicheFiltre.setEditable(true);
    }
    public void updateReference(){
        List<Map<String, Object>> references = dependencyManager.getFicheStockRepository().getAllId();
        List<String> referencesList = new ArrayList<>();

        for(Map<String, Object> c : references){
            referencesList.add((String)c.get("id_matierePremiere"));
        }
        referenceFicheFiltre.setItems(FXCollections.observableArrayList(referencesList));
        referenceFicheFiltre.setEditable(true);
    }
    public void updateTableauBesoins(String reference, String categorie, String designation){
        if (!besoinsTable.getItems().isEmpty()) {
            besoinsTable.getItems().clear();
        }

        if (!besoinsTable.getColumns().isEmpty()) {
            besoinsTable.getColumns().clear();
        }

        ObservableList<Map<String, Object>> observableBesoins = FXCollections.observableArrayList(tableauBesoins);
        besoinsTable.setItems(observableBesoins);
        TableColumn<Map<String, Object>, String> temp = new TableColumn<>("Reference produit");
        temp.setId("refProduitCol");
        temp.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("idProduit")).asString());
        besoinsTable.getColumns().add(temp);

        temp = new TableColumn<>("Quantité produit");
        temp.setId("qteProduitCol");
        temp.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantiteProduit")).asString());
        besoinsTable.getColumns().add(temp);


        /* Rajouter les columns au tableau */
        for(Map<String, Object> mp : this.mps) {
            //System.out.println(mp);
            if (categorie == null && reference != null && designation != null) {
                if (mp.get("id_reference").toString().equals(reference) && mp.get("designation").toString().equals(designation)) {
                    TableColumn<Map<String, Object>, String> temp_bis = new TableColumn<>(mp.get("id_reference").toString());
                    besoinsTable.getColumns().add(temp_bis);
                    temp_bis.setCellValueFactory(cellData -> {
                        Object value = cellData.getValue().get(mp.get("id_reference").toString());
                        return new SimpleObjectProperty<>(value != null ? value.toString() : "---");
                    });
                }
            } else if (reference == null && categorie != null && designation != null) {
                if (mp.get("categorie").toString().equals(categorie) && mp.get("designation").toString().equals(designation)) {
                    TableColumn<Map<String, Object>, String> temp_bis = new TableColumn<>(mp.get("id_reference").toString());
                    besoinsTable.getColumns().add(temp_bis);
                    temp_bis.setCellValueFactory(cellData -> {
                        Object value = cellData.getValue().get(mp.get("id_reference").toString());
                        return new SimpleObjectProperty<>(value != null ? value.toString() : "---");
                    });
                }
            } else if (designation == null && categorie != null && reference != null) {
                if (mp.get("categorie").toString().equals(categorie) && mp.get("id_reference").toString().equals(reference)) {
                    TableColumn<Map<String, Object>, String> temp_bis = new TableColumn<>(mp.get("id_reference").toString());
                    besoinsTable.getColumns().add(temp_bis);
                    temp_bis.setCellValueFactory(cellData -> {
                        Object value = cellData.getValue().get(mp.get("id_reference").toString());
                        return new SimpleObjectProperty<>(value != null ? value.toString() : "---");
                    });
                }
            } else if (reference == null && categorie == null && designation != null) {
                if (mp.get("designation").toString().equals(designation)) {
                    TableColumn<Map<String, Object>, String> temp_bis = new TableColumn<>(mp.get("id_reference").toString());
                    besoinsTable.getColumns().add(temp_bis);
                    temp_bis.setCellValueFactory(cellData -> {
                        Object value = cellData.getValue().get(mp.get("id_reference").toString());
                        return new SimpleObjectProperty<>(value != null ? value.toString() : "---");
                    });
                }
            } else if (reference == null && designation == null && categorie != null) {
                if (mp.get("categorie").toString().equals(categorie)) {
                    TableColumn<Map<String, Object>, String> temp_bis = new TableColumn<>(mp.get("id_reference").toString());
                    besoinsTable.getColumns().add(temp_bis);
                    temp_bis.setCellValueFactory(cellData -> {
                        Object value = cellData.getValue().get(mp.get("id_reference").toString());
                        return new SimpleObjectProperty<>(value != null ? value.toString() : "---");
                    });
                }
            } else if (categorie == null && designation == null && reference != null) {
                if (mp.get("id_reference").toString().equals(reference)) {
                    TableColumn<Map<String, Object>, String> temp_bis = new TableColumn<>(mp.get("id_reference").toString());
                    besoinsTable.getColumns().add(temp_bis);
                    temp_bis.setCellValueFactory(cellData -> {
                        Object value = cellData.getValue().get(mp.get("id_reference").toString());
                        return new SimpleObjectProperty<>(value != null ? value.toString() : "---");
                    });
                }
            } else if (reference == null && categorie == null && designation == null){
                TableColumn<Map<String, Object>, String> temp_bis = new TableColumn<>(mp.get("id_reference").toString());
                besoinsTable.getColumns().add(temp_bis);
                temp_bis.setCellValueFactory(cellData -> {
                    Object value = cellData.getValue().get(mp.get("id_reference").toString());
                    return new SimpleObjectProperty<>(value != null ? value.toString() : "---");
                });
            }
        }
    }

    public void retour(ActionEvent event) throws IOException, SQLException {
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }
    public void rechercheFiche(ActionEvent event) throws IOException, SQLException {
        String categorie = categorieFicheFiltre.getValue();
        String desgination = designationFicheFiltre.getValue();
        String reference = referenceFicheFiltre.getValue();
        if (categorie != null && categorie.isEmpty()) categorie = null;
        if (desgination != null && desgination.isEmpty()) desgination = null;
        if (reference != null && reference.isEmpty()) reference = null;
        updateTableauBesoins(reference, categorie, desgination);
    }
}
