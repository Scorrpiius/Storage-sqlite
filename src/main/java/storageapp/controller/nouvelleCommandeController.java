package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class nouvelleCommandeController {
    private String produitInit;
    @FXML
    private TextArea descriptionCommande;
    @FXML
    private DatePicker dateCommandePicker;
    @FXML
    private ComboBox<String> refProduitBox;
    private final DependencyManager dependencyManager;
    @FXML
    private TextField qteProduitField, idCommandeField;
    @FXML
    private TableView<Map<String, Object>> produitTable;
    private final List<Map<String, Object>> listeProduits;
    @FXML
    private TableColumn<Map<String, Object>, String> idProduitCol, qteProduitCol;


    public nouvelleCommandeController(DependencyManager dependencyManager){
        this.dependencyManager = dependencyManager;
        this.listeProduits = new ArrayList<>();
    }
    public void initialize(){
        updateProduit();
    }
    public void updateProduit(){
        List<Map<String, Object>> produits = dependencyManager.getProduitFiniRepository().findAll();
        List<String> produitList = new ArrayList<>();

        for(Map<String, Object> c : produits){
            produitList.add((String)c.get("reference"));
        }
        refProduitBox.setItems(FXCollections.observableArrayList(produitList));
        refProduitBox.setEditable(true);
    }
    public void updateProduitTable(){
        if (!produitTable.getItems().isEmpty()) {
            produitTable.getItems().clear();
        }

        ObservableList<Map<String, Object>> observableProduits = FXCollections.observableArrayList(listeProduits);
        produitTable.setItems(observableProduits);

        idProduitCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_ProduitFini")).asString());
        qteProduitCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());

        produitTable.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    produitInit = (String) row.getItem().get("id_ProduitFini");
                    updateData(produitInit, event);
                }
            });
            return row;
        });
    }
    public void updateData(String produitId, MouseEvent ignoredEvent){
        Map<String, Object> p = listeProduits.stream()
                .filter(map -> produitId.equals(map.get("id_ProduitFini")))
                .findFirst()
                .get();

        refProduitBox.setValue(produitId);
        qteProduitField.setText(String.valueOf(p.get("quantite")));
    }
    public void ajouter(){
        String idProduit = refProduitBox.getValue();
        String quantite = qteProduitField.getText();

        /* Vérifier que cette référence n'existe pas déjà dans la liste */
        Optional<Map<String, Object>> result = listeProduits.stream()
                .filter(map -> idProduit.equals(map.get("id_ProduitFini")))
                .findFirst();

        if(result.isPresent()){
            System.out.println("Can't add the same reference twice");
            return;
        }

        Map<String, Object> mp = new HashMap<>();
        mp.put("id_ProduitFini", idProduit);
        mp.put("quantite", quantite);

        listeProduits.add(mp);
        updateProduitTable();

    }
    public void modifier(){
        String newProduit = refProduitBox.getValue();
        String quantite = qteProduitField.getText();

        if (produitInit == null){
            System.out.println("Click on the line to modify");
            return;
        }

        if(!produitInit.equals(newProduit)) {
            Optional<Map<String, Object>> result = listeProduits.stream()
                    .filter(map -> newProduit.equals(map.get("id_ProduitFini")))
                    .findFirst();

            if (result.isPresent()) {
                System.out.println("Already exists in the table");
                return;
            }
        }

        Map<String, Object> p = listeProduits.stream()
                .filter(map -> produitInit.equals(map.get("id_ProduitFini")))
                .findFirst()
                .get();
        p.put("id_ProduitFini", newProduit);
        p.put("quantite", quantite);

        updateProduitTable();
    }
    public void supprimerProduit(){
        Optional<Map<String, Object>> p = listeProduits.stream()
                .filter(map -> refProduitBox.getValue().equals(map.get("id_ProduitFini")))
                .findFirst();
        if(p.isEmpty()){
            System.out.println("Doesn't exist");
            return;
        } else {
            listeProduits.remove(p.get());
            updateProduitTable();
        }
    }
    public void finaliserSaisie(ActionEvent e) throws SQLException {
        final String idCommande = idCommandeField.getText();
        final String descriptionCommande = this.descriptionCommande.getText();
        final LocalDate dateCommande = dateCommandePicker.getValue();


        if (!(dependencyManager.getCommandeRepository().findById(idCommande) == null)){
            showAlert("Cette référence de commande existe déjà. Veuillez saisir une nouvelle référence");
            return;
        }

        dependencyManager.getCommandeRepository().create(idCommande, descriptionCommande, dateCommande);

        /* Créer le tableau de bord en même temps
         * Chercher les produits associés à la commande
         * Pour chaque produit, faire la liste des matières premières et calculer le total (qte produit * nbr de matières premières) */


        /*List<Map<String, Object>> listeBesoins = new ArrayList<Map<String, Object>>();

        for (Map<String, Object> p : listeProduits){

            List<Map<String, Object>> listeMateriaux = dependencyManager.getProduitFiniMatierePremiereRepository().findByProduitId((String) p.get("id_ProduitFini"));

            for(Map<String, Object> matiere : listeMateriaux){
                int qteTotale = (Integer) matiere.get("quantite") * Integer.parseInt(p.get("quantite").toString());
                Map<String, Object> temp = new HashMap<>();
                temp.put("reference", (String) matiere.get("matiere_id"));
                temp.put("quantite", qteTotale);
                int update = 0;

                // Vérifier si la ref est déjà dans la liste, si oui ajouter au nombre sinon ajouter la nouvelle matière première
                for(Map<String,Object> besoin : listeBesoins){
                    if(besoin.get("reference").equals(matiere.get("matiere_id"))){
                        int newQte = (Integer) besoin.get("quantite") + qteTotale;
                        besoin.put("quantite", newQte);
                        update = 1;
                    }
                }
                if(update == 0){
                    listeBesoins.add(temp);
                }
            }
        }

        for(Map<String, Object> besoin : listeBesoins){
            dependencyManager.getCommandeMatierePremiereRepository().create(
                    idCommande,
                    besoin.get("reference").toString(),
                    Integer.parseInt(besoin.get("quantite").toString())
            );
        }*/

        for(Map<String, Object> p : listeProduits){
            dependencyManager.getCommandeProduitFiniRepository().create(idCommande, p.get("id_ProduitFini").toString(), Integer.parseInt(p.get("quantite").toString()));
        }

        dependencyManager.getConnection().commit();
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

    }
    public void retour(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }

    private void showAlert(String contentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Validation échouée");
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
