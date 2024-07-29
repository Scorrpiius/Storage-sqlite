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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class modifierCommandeController {
    private final String IDCOMMANDE;
    @FXML
    private TextArea descriptionCommande;
    @FXML
    private DatePicker dateCommandePicker;
    @FXML
    private ComboBox<String> refProduitBox;
    private String idProduitInit, idCommandeInit;
    private final DependencyManager dependencyManager;
    @FXML
    private TextField qteProduitField, idCommandeField;
    @FXML
    private TableView<Map<String, Object>> produitTable;
    @FXML
    private TableColumn<Map<String, Object>, String> idProduitCol, qteProduitCol;


    public modifierCommandeController(DependencyManager dependencyManager, String commandeId){
        this.dependencyManager = dependencyManager;
        this.idCommandeInit = commandeId;
        this.IDCOMMANDE = commandeId;
    }
    public void initialize(){
        updateProduit();
        updateProduitTable();
        updateAllData();
    }
    public void updateAllData(){
        Map<String, Object> commande = dependencyManager.getCommandeRepository().findById(idCommandeInit);
        idCommandeField.setText(idCommandeInit);
        dateCommandePicker.setValue(LocalDate.parse(commande.get("date").toString()));
        descriptionCommande.setText((String) commande.get("description"));
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

        List<Map<String, Object>> produits = dependencyManager.getCommandeProduitFiniRepository().findByCommandeId(idCommandeInit);
        ObservableList<Map<String, Object>> observableProduits = FXCollections.observableArrayList(produits);
        produitTable.setItems(observableProduits);

        idProduitCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_ProduitFini")).asString());
        qteProduitCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());

        produitTable.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    idProduitInit = (String) row.getItem().get("id_ProduitFini");
                    updateData(idProduitInit, event);
                }
            });
            return row;
        });
    }
    public void updateData(String produitId, MouseEvent ignoredEvent){
        Map<String, Object> produit = dependencyManager.getCommandeProduitFiniRepository().findByIds(idCommandeInit, produitId);
        int quantite = (int) produit.get("quantite");
        refProduitBox.setValue(produitId);
        qteProduitField.setText(String.valueOf(quantite));
    }
    public void retour(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }
    public void finaliserSaisie(ActionEvent e) throws SQLException {
        if(!IDCOMMANDE.equals(idCommandeField.getText())){
            if (!(dependencyManager.getCommandeRepository().findById(idCommandeField.getText()) == null)){
                showAlert();
                return;
            }
        }

        majId();
        dependencyManager.getCommandeRepository().updateInfos(idCommandeInit, descriptionCommande.getText(), dateCommandePicker.getValue());
        dependencyManager.getConnection().commit();
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

    }
    public void modifier(){
        majId();

        String newProduit = refProduitBox.getValue();
        String quantite = qteProduitField.getText();
        dependencyManager.getCommandeProduitFiniRepository().update(idCommandeInit, idProduitInit, newProduit, quantite);
        updateProduitTable();
    }
    public void supprimer(){
        majId();

        String produitToDelete = refProduitBox.getValue();
        /* Supprimer le lien produit commande */
        dependencyManager.getCommandeProduitFiniRepository().delete(idCommandeInit, produitToDelete);
        updateProduitTable();
    }
    public void ajouter(){
        majId();

        String idProduit = refProduitBox.getValue();
        String quantite = qteProduitField.getText();

        dependencyManager.getCommandeProduitFiniRepository().create(idCommandeInit, idProduit, Integer.parseInt(quantite));

        updateProduitTable();
    }
    public void majId(){
        String idCommandeNouveau = idCommandeField.getText();

        /* Si les id sont différents faire la maj sinon ne rien faire */
        if (!idCommandeNouveau.equals(idCommandeInit)){

            /* MAJ */
            dependencyManager.getCommandeRepository().updateCommandeId(idCommandeInit, idCommandeNouveau);
            if (!(dependencyManager.getCommandeProduitFiniRepository().findByCommandeId(idCommandeInit) == null)) {
                dependencyManager.getCommandeProduitFiniRepository().updateCommandeId(idCommandeInit, idCommandeNouveau);
            }
        }
        idCommandeInit = idCommandeNouveau;
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Validation échouée");
        alert.setContentText("Cette référence de commande existe déjà. Veuillez saisir une nouvelle référence");
        alert.showAndWait();
    }
}
