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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class modifierMatierePremiereController {
    private String refInit;
    private final String factureId;
    @FXML
    private TextField qte, pxUnit;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> entreeTable;
    @FXML
    private ComboBox<String> categorieBox, referenceBox, designationBox, uniteMesureBox;
    @FXML
    private TableColumn<Map<String, Object>, String> refColumn, catColumn, desColumn, qteColumn, pxUnitColumn, pxRevientColumn, uniteMesureColumn;

    public modifierMatierePremiereController(DependencyManager dependencyManager, String factureId){
        this.dependencyManager = dependencyManager;
        this.factureId = factureId;
    }

    public void initialize(){
        updateEntreeTable();
    }

    @FXML
    public void updateEntreeTable(){
        if (!entreeTable.getItems().isEmpty()) {
            entreeTable.getItems().clear();
        }

        List<Map<String, Object>> entrees = dependencyManager.getEntreeRepository().findByFactureId(factureId);
        ObservableList<Map<String, Object>> observableEntrees = FXCollections.observableArrayList(entrees);
        entreeTable.setItems(observableEntrees);

        refColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_reference")).asString());
        catColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("categorie")).asString());
        desColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("designation")).asString());
        qteColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());
        pxUnitColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("px_unitaire")).asString());
        pxRevientColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("px_revient")).asString());

        entreeTable.setRowFactory(tv -> {
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

    public void updateData(String matierePremiereId, MouseEvent event){
        updateCategorie();
        updateDesignation();
        updateUniteMesure();
        this.refInit = matierePremiereId;
        Map<String, Object> matierePremiere = dependencyManager.getEntreeRepository().findById(matierePremiereId);
        System.out.println(matierePremiere);

        String categorie = (String) matierePremiere.get("categorie");
        String designation = (String) matierePremiere.get("designation");
        int quantite = (int) matierePremiere.get("quantite");
        String uniteMesure = (String) matierePremiere.get("uniteMesure");
        double pxUnit = (double) matierePremiere.get("px_unitaire");

        this.categorieBox.setValue(categorie);
        this.designationBox.setValue(designation);
        this.qte.setText(String.valueOf(quantite));
        this.referenceBox.setValue(matierePremiereId);
        this.uniteMesureBox.setValue(uniteMesure);
        this.pxUnit.setText(String.valueOf(pxUnit));

        this.categorieBox.setEditable(true);
        this.designationBox.setEditable(true);
        this.qte.setEditable(true);
        this.uniteMesureBox.setEditable(true);
        this.pxUnit.setEditable(true);
    }

    public void updateCategorie(){
        List<Map<String, Object>> categories = dependencyManager.getCategorieRepository().findAll();
        List<String> categoriesList = new ArrayList<>();

        for(Map<String, Object> c : categories){
            categoriesList.add((String)c.get("nom"));
        }
        categorieBox.setItems(FXCollections.observableArrayList(categoriesList));
        categorieBox.setEditable(true);
    }

    @FXML
    public void updateDesignation(){
        List<Map<String, Object>> designations = dependencyManager.getDesignationRepository().findAll();
        List<String> designationsList = new ArrayList<>();

        for(Map<String, Object> c : designations){
            designationsList.add((String)c.get("nom"));
        }
        designationBox.setItems(FXCollections.observableArrayList(designationsList));
        designationBox.setEditable(true);
    }

    @FXML
    public void updateUniteMesure(){
        List<Map<String, Object>> uniteMesures = dependencyManager.getUniteMesureRepository().findAll();
        List<String> uniteMesuresList = new ArrayList<>();
        for(Map<String, Object> c : uniteMesures){
            uniteMesuresList.add((String)c.get("nom"));
        }
        uniteMesureBox.setItems(FXCollections.observableArrayList(uniteMesuresList));
        uniteMesureBox.setEditable(true);
    }

    @FXML
    public void supprimerMatPrem(ActionEvent e) throws SQLException {
        String idRef = referenceBox.getValue();
        int qte = Integer.parseInt(this.qte.getText());
        int initQte = (int) dependencyManager.getFicheStockRepository().findById(idRef).get("quantite");
        System.out.println(qte);
        System.out.println(initQte);
        int q = initQte - qte;
        System.out.println(q);
        dependencyManager.getFicheStockRepository().update(idRef, null, q,null,null);
        dependencyManager.getEntreeRepository().delete(idRef, factureId);
        updateEntreeTable();
    }

    public void finirModifications(ActionEvent event) throws IOException, SQLException {
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }

    public void validerModifications(ActionEvent event) throws IOException, SQLException {
        final String categorie = categorieBox.getValue();
        final String reference = referenceBox.getValue();
        final String designation = designationBox.getValue();
        final String quantite = qte.getText();
        final String pxUnit = this.pxUnit.getText();
        final String uniteMesure = uniteMesureBox.getValue();

        final boolean isValid = categorie != null && reference != null  && designation != null && quantite != null;

        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Validation échouée");
            alert.setContentText("Veuillez remplir tous les champs");
            alert.showAndWait();
            return;
        }

        dependencyManager.getEntreeRepository().update(factureId,refInit, reference, categorie, designation, quantite,pxUnit, uniteMesure);
        dependencyManager.getFicheStockRepository().update(refInit, reference, Integer.parseInt(quantite), categorie, designation);
        updateEntreeTable();
    }
}
