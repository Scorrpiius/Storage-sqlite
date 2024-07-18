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
import java.util.List;
import java.util.Map;

public class modifierEntreeController {
    private final DependencyManager dependencyManager;
    private String refInit;
    @FXML
    private TableView<Map<String, Object>> listeEntree;
    @FXML
    private TableColumn<Map<String, Object>, String> referenceCol, categorieCol, designationCol, quantiteCol, pxUnitaireCol, pxRevientCol, uniteMesureCol;
    @FXML
    private TextField qteField, pxUnitField;
    @FXML
    private ComboBox<String> categorieField, referenceField, designationField, unitMesureField;
    public modifierEntreeController(DependencyManager dependencyManager){
        this.dependencyManager = dependencyManager;
    }

    public void initialize(){
        updateEntreeTable();
    }

    @FXML
    public void updateEntreeTable(){
        if (!listeEntree.getItems().isEmpty()) {
            listeEntree.getItems().clear();
        }

        List<Map<String, Object>> entrees = dependencyManager.getEntreeRepository().findByFactureId(null);
        ObservableList<Map<String, Object>> observableEntrees = FXCollections.observableArrayList(entrees);
        listeEntree.setItems(observableEntrees);

        referenceCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_reference")).asString());
        categorieCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("categorie")).asString());
        designationCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("designation")).asString());
        quantiteCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());
        pxUnitaireCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("px_unitaire")).asString());
        pxRevientCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("px_revient")).asString());

        for (Map<String, Object> f : entrees) {

            listeEntree.setRowFactory(tv -> {
                TableRow<Map<String, Object>> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty()) {
                        String selectEntreeId = (String) row.getItem().get("id_reference");
                        updateData(selectEntreeId, event);
                    }
                });
                return row;
            });
        }
    }

    public void updateData(String matierePremiereId, MouseEvent event){
        this.refInit = matierePremiereId;
        Map<String, Object> matierePremiere = dependencyManager.getEntreeRepository().findById(matierePremiereId);
        System.out.println(matierePremiere);

        String categorie = (String) matierePremiere.get("categorie");
        String designation = (String) matierePremiere.get("designation");
        int quantite = (int) matierePremiere.get("quantite");
        String uniteMesure = (String) matierePremiere.get("uniteMesure");
        double pxUnit = (double) matierePremiere.get("px_unitaire");

        this.categorieField.setValue(categorie);
        this.designationField.setValue(designation);
        this.qteField.setText(String.valueOf(quantite));
        this.referenceField.setValue(matierePremiereId);
        this.unitMesureField.setValue(uniteMesure);
        this.pxUnitField.setText(String.valueOf(pxUnit));

        this.categorieField.setEditable(true);
        this.designationField.setEditable(true);
        this.qteField.setText(String.valueOf(quantite));
        this.referenceField.setValue(matierePremiereId);
        this.unitMesureField.setValue(uniteMesure);
        this.pxUnitField.setText(String.valueOf(pxUnit));

    }

    public void finirModifications(ActionEvent event) throws IOException, SQLException {
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }

    public void validerModifications(ActionEvent event) throws IOException, SQLException {
        final String categorie = categorieField.getValue();
        final String reference = referenceField.getValue();
        final String designation = designationField.getValue();
        final String quantite = qteField.getText();
        final String pxUnit = pxUnitField.getText();
        final String uniteMesure = unitMesureField.getValue();


        final boolean isValid = categorie != null && reference != null  && designation != null && quantite != null;

        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Validation échouée");
            alert.setContentText("Veuillez remplir tous les champs");
            alert.showAndWait();
            return;
        }
        /* Modifier la matière première, la fiche de stock qui a été crée, et la catégorie/désignation si elles ont changés*/
        /*Update matière première*/
        dependencyManager.getEntreeRepository().update(refInit, reference, categorie, designation, quantite,pxUnit, uniteMesure);
        System.out.println(dependencyManager.getEntreeRepository().findById(reference));
        /* Update fiche de stock */
        dependencyManager.getFicheStockRepository().update(refInit, reference, Integer.parseInt(quantite), categorie, designation);

    }

}
