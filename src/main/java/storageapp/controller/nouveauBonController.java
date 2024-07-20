package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class nouveauBonController {
    private int id, refMatSortirInit, selectSortieId;
    private String idBonInit, idBonNouveau;
    @FXML
    private TextField idBonField, qteMatStock, qteMatSortir ;
    @FXML
    private TextArea descMatSortir;
    @FXML
    private ComboBox<String> refMatPremSortir;
    @FXML
    private DatePicker dateBon;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> sortiesTableau;
    @FXML
    private TableColumn<Map<String, Object>, String> idColumn, refColumn, qteColumn, descColumn;

    public nouveauBonController(DependencyManager dependencyManager){
        this.dependencyManager = dependencyManager;
        this.id = 1;
    }

    public void initialize(){
        updateReference();
    }

    public void retour(ActionEvent event) throws IOException, SQLException {
        dependencyManager.getConnection().rollback();
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }

    public void modifier(ActionEvent event) throws IOException {
        String idMatPrem = refMatPremSortir.getValue();
        String quantite = qteMatSortir.getText();
        String description = this.descMatSortir.getText();

        dependencyManager.getSortieRepository().update(selectSortieId,idBonField.getText(), Integer.parseInt(quantite),idMatPrem,description);
        updateSortiesTable();
    }

    @FXML
    public void updateSortiesTable(){
        if (!sortiesTableau.getItems().isEmpty()) {
            sortiesTableau.getItems().clear();
        }

        List<Map<String, Object>> sorties = dependencyManager.getSortieRepository().getAllSortiesByBon(idBonField.getText());
        System.out.println(sorties);
        ObservableList<Map<String, Object>> observableSorties = FXCollections.observableArrayList(sorties);
        sortiesTableau.setItems(observableSorties);

        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id")).asString());
        refColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_matierePremiere")).asString());
        qteColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());
        descColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("description")).asString());

        sortiesTableau.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    selectSortieId = (int) row.getItem().get("id");
                    updateData(selectSortieId, event);
                }
            });
            return row;
        });
    }
    @FXML
    private void updateData(int selectSortieId, MouseEvent event) {
        this.refMatSortirInit = selectSortieId;
        Map<String, Object> sortie = dependencyManager.getSortieRepository().findById(refMatSortirInit, idBonField.getText());
        Map<String, Object> ficheStock = dependencyManager.getFicheStockRepository().findById((String) sortie.get("id_matierePremiere"));

        String reference = (String) sortie.get("id_matierePremiere");
        String description = (String) sortie.get("description");
        int quantite = (int) sortie.get("quantite");
        int quantiteInit = (int) ficheStock.get("quantite");

        this.refMatPremSortir.setValue(reference);
        this.descMatSortir.setText(description);
        this.qteMatSortir.setText(String.valueOf(quantite));
        this.qteMatStock.setText(String.valueOf(quantiteInit));

    }
    @FXML
    public void updateReference(){
        List<Map<String, Object>> references = dependencyManager.getFicheStockRepository().getAllId();
        List<String> referencesList = new ArrayList<>();

        for(Map<String, Object> c : references){
            referencesList.add((String)c.get("id_matierePremiere"));
        }
        refMatPremSortir.setItems(FXCollections.observableArrayList(referencesList));
        refMatPremSortir.setEditable(true);
    }
    @FXML
    public void updateQuantite(ActionEvent event){
        Map<String, Object> ficheStock = dependencyManager.getFicheStockRepository().findById(refMatPremSortir.getValue());
        qteMatStock.setText(String.valueOf(ficheStock.get("quantite")));
    }

    public void ajouter(ActionEvent ignoredEvent) throws IOException, SQLException {
        idBonInit = idBonField.getText();

        String idMatPrem = refMatPremSortir.getValue();
        String quantite = qteMatSortir.getText();
        String description = this.descMatSortir.getText();
        int nouvelleQuantite = Integer.parseInt(qteMatStock.getText()) - Integer.parseInt(quantite);

        if (nouvelleQuantite < 0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Validation échouée");
            alert.setContentText("Quantité invalide");
            alert.showAndWait();
            return;
        }

        dependencyManager.getSortieRepository().create(this.id, idBonInit, idMatPrem, description, Integer.parseInt(quantite));
        updateSortiesTable();
        id++;
    }

    public void finaliserSaisie(ActionEvent event) throws SQLException, IOException {
        //idBonNouveau = idBonField.getText();
        final LocalDate date = this.dateBon.getValue();

        final boolean isValid = idBonField.getText()!= null && date != null;

        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Validation échouée");
            alert.setContentText("Veuillez remplir tous les champs");
            alert.showAndWait();
            return;
        }

        List<Map<String, Object>> sorties = dependencyManager.getSortieRepository().getAllSortiesByBon(idBonField.getText());
        for(Map<String, Object> sortie : sorties){
            int qte = (int) sortie.get("quantite");
            String refMatierePremiere = (String) sortie.get("id_matierePremiere");

            Map<String, Object> ficheStock = dependencyManager.getFicheStockRepository().findById(refMatierePremiere);
            int nouvelleQte = (int) ficheStock.get("quantite") - qte;

            dependencyManager.getFicheStockRepository().update(refMatierePremiere, null, nouvelleQte, null, null);

        }
        dependencyManager.getBonSortieRepository().create(idBonField.getText(), date);
        //dependencyManager.getSortieRepository().update(idBonInit,idBonNouveau);

        dependencyManager.getConnection().commit();
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void supprimerSortie(ActionEvent ignoredEvent){
        //Mettre à jour les identifiants dans le tableau également (ou pas je sais pas si c'est important, ca reste un id unique à la fin)
        dependencyManager.getSortieRepository().delete(String.valueOf(refMatSortirInit), idBonField.getText());
        updateSortiesTable();
    }

}
