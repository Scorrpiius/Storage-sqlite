package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class tableauBesoinController {
    @FXML
    private AnchorPane root;
    private String idCommande;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> besoinsTable;
    private final List<Map<String, Object>> mps;
    private final List<Map<String, Object>> listeProduits;
    private final List<Map<String, Object>> tableauBesoins;
    @FXML
    private ComboBox<String> referenceFicheFiltre, categorieFicheFiltre, designationFicheFiltre;

    public  tableauBesoinController(DependencyManager dependencyManager, String idCommande, AnchorPane root){
        this.dependencyManager = dependencyManager;
        this.listeProduits = dependencyManager.getCommandeProduitFiniRepository().findByCommandeId(idCommande);
        this.mps = new ArrayList<>();
        this.tableauBesoins = new ArrayList<>();
        this.root = root;
        this.idCommande = idCommande;
    }
    public void initialize(){

        updateCategorie();
        updateDesignation();
        updateReference();
        besoinsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        for (Map<String, Object> p : listeProduits) {
            int quantiteProduit = Integer.parseInt(p.get("quantite").toString());
            String idProduit = p.get("id_ProduitFini").toString();
            List<Map<String, Object>> listeMPs = dependencyManager.getProduitFiniMatierePremiereRepository().findByProduitId(idProduit);

            Map<String, Object> ligne = new HashMap<>();
            ligne.put("idProduit", idProduit);
            ligne.put("quantiteProduit", quantiteProduit);

            for (Map<String, Object> mp : listeMPs) {
                int temp = Integer.parseInt(mp.get("quantite").toString());
                ligne.put(mp.get("matiere_id").toString(), temp * quantiteProduit );
                System.out.println(mp.get("matiere_id").toString());
                this.mps.add(dependencyManager.getEntreeRepository().getCategorieDesignation(mp.get("matiere_id").toString()));
            }
            tableauBesoins.add(ligne);
        }
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
            String columnKey = mp.get("id_reference").toString();

            // Vérifie si la colonne existe déjà
            boolean columnExists = besoinsTable.getColumns().stream()
                    .anyMatch(col -> col.getText().equals(columnKey));

            if (!columnExists) {
                boolean shouldAddColumn = false;

                if (reference != null && reference.equals(mp.get("id_reference").toString())) {
                    shouldAddColumn = (categorie == null || categorie.equals(mp.get("categorie").toString())) &&
                            (designation == null || designation.equals(mp.get("designation").toString()));
                } else if (categorie != null && categorie.equals(mp.get("categorie").toString())) {
                    shouldAddColumn = (reference == null || reference.equals(mp.get("id_reference").toString())) &&
                            (designation == null || designation.equals(mp.get("designation").toString()));
                } else if (designation != null && designation.equals(mp.get("designation").toString())) {
                    shouldAddColumn = (reference == null || reference.equals(mp.get("id_reference").toString())) &&
                            (categorie == null || categorie.equals(mp.get("categorie").toString()));
                } else if (reference == null && categorie == null && designation == null) {
                    shouldAddColumn = true;
                }

                if (shouldAddColumn) {
                    addColumn(columnKey);
                }
            }
        }
    }
    private void addColumn(String columnKey) {
        TableColumn<Map<String, Object>, String> column = new TableColumn<>(columnKey);
        besoinsTable.getColumns().add(column);
        column.setCellValueFactory(cellData -> {
            Object value = cellData.getValue().get(columnKey);
            return new SimpleObjectProperty<>(value != null ? value.toString() : "---");
        });
    }
    public void retourCommande(ActionEvent event) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("commande.fxml"));
        fxmlLoader.setController(new commandeController(dependencyManager,idCommande, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setMinHeight(root.getHeight());
        newLoadedPane.setMinWidth(root.getWidth());
        root.getChildren().setAll(newLoadedPane);
    }
    public void rechercheFiche(ActionEvent ignoredEvent) {
        String categorie = categorieFicheFiltre.getValue();
        String desgination = designationFicheFiltre.getValue();
        String reference = referenceFicheFiltre.getValue();
        if (categorie != null && categorie.isEmpty()) categorie = null;
        if (desgination != null && desgination.isEmpty()) desgination = null;
        if (reference != null && reference.isEmpty()) reference = null;
        updateTableauBesoins(reference, categorie, desgination);
    }
}
