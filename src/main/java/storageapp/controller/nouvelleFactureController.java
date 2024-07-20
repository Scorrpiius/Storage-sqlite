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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class nouvelleFactureController {

    private int nbrEntree;
    @FXML
    private DatePicker dateFacture;
    private String idInit;
    private String idNouveau, img, refMatInit;
    @FXML
    private ImageView matiereImg;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> matPremTableau;
    @FXML
    private ComboBox<String> nomDevise, fournisseurFacture, refMatPremBox, catMatPremBox, desMatPremBox, uniteMesureMatPremBox;
    @FXML
    private TextField referenceFacture, valeurDevise, txTheo, txReel, qteMatPremField, pxUnitMatPremField;
    @FXML
    private TableColumn<Map<String, Object>, String> refMatPremCol, desMatPremCol, catMatPremCol, qteMatPremCol, pxUnitMatPremCol, pxRevientDeviseMatPremColumn, pxRevientLocalMatPremColumn;

    public nouvelleFactureController(DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
        this.nbrEntree = 0;
    }
    public void initialize(){
        updateDevise();
        updateFournisseur();
        updateCategorie();
        updateDesignation();
        updateReference();
        updateUniteMesure();
    }
    @FXML
    public void updateCategorie(){
        List<Map<String, Object>> categories = dependencyManager.getCategorieRepository().findAll();
        List<String> categoriesList = new ArrayList<>();

        for(Map<String, Object> c : categories){
            categoriesList.add((String)c.get("nom"));
        }
        catMatPremBox.setItems(FXCollections.observableArrayList(categoriesList));
        catMatPremBox.setEditable(true);
    }
    @FXML
    public void updateDesignation(){
        List<Map<String, Object>> designations = dependencyManager.getDesignationRepository().findAll();
        List<String> designationsList = new ArrayList<>();

        for(Map<String, Object> c : designations){
            designationsList.add((String)c.get("nom"));
        }
        desMatPremBox.setItems(FXCollections.observableArrayList(designationsList));
        desMatPremBox.setEditable(true);
    }
    @FXML
    public void updateReference(){
        List<Map<String, Object>> references = dependencyManager.getFicheStockRepository().getAllId();
        List<String> referencesList = new ArrayList<>();

        for (Map<String, Object> c : references) {
            referencesList.add((String) c.get("id_matierePremiere"));
        }
        refMatPremBox.setItems(FXCollections.observableArrayList(referencesList));
        refMatPremBox.setEditable(true);
    }
    @FXML
    public void updateUniteMesure(){
        List<Map<String, Object>> uniteMesures = dependencyManager.getUniteMesureRepository().findAll();
        List<String> uniteMesuresList = new ArrayList<>();
        for(Map<String, Object> c : uniteMesures){
            uniteMesuresList.add((String)c.get("nom"));
        }
        uniteMesureMatPremBox.setItems(FXCollections.observableArrayList(uniteMesuresList));
        uniteMesureMatPremBox.setEditable(true);
    }
    public void loadImage(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier PNG");

        File selectedFile = fileChooser.showOpenDialog(currentStage);
        InputStream stream = new FileInputStream(selectedFile);
        Image image = new Image(stream);
        matiereImg.setImage(image);

        this.img = String.valueOf(selectedFile);
    }
    @FXML
    public void updateFournisseur(){
        List<Map<String, Object>> fournisseurs = dependencyManager.getFournisseurRepository().findAll();
        List<String> fournisseursList = new ArrayList<>();

        for(Map<String, Object> c : fournisseurs){
            fournisseursList.add((String)c.get("nom"));
        }
        fournisseurFacture.setItems(FXCollections.observableArrayList(fournisseursList));
        fournisseurFacture.setEditable(true);
    }
    @FXML
    public void updateDevise(){
        List<Map<String, Object>> devises = dependencyManager.getDeviseRepository().findAll();
        List<String> devisesList = new ArrayList<>();

        for(Map<String, Object> c : devises){
            devisesList.add((String)c.get("nom"));
        }
        nomDevise.setItems(FXCollections.observableArrayList(devisesList));
        nomDevise.setEditable(true);
    }
    @FXML
    public void updateEntreeTable(){
        if (idInit == null) {
            idInit = referenceFacture.getText();
        } else {
            idNouveau = referenceFacture.getText();
        }

        if (!matPremTableau.getItems().isEmpty()) {
            matPremTableau.getItems().clear();
        }
        final String nom_devise = nomDevise.getValue();
        pxRevientDeviseMatPremColumn.setText("Prix de revient \n("+nom_devise+")");

        List<Map<String, Object>> entrees = dependencyManager.getEntreeRepository().findByFactureId(idNouveau);
        ObservableList<Map<String, Object>> observableEntrees = FXCollections.observableArrayList(entrees);
        matPremTableau.setItems(observableEntrees);

        refMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_reference")).asString());
        desMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("designation")).asString());
        catMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("categorie")).asString());
        qteMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());
        pxUnitMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("px_unitaire")).asString());
        pxRevientDeviseMatPremColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("px_revient_devise")).asString());
        pxRevientLocalMatPremColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("px_revient_local")).asString());

        matPremTableau.setRowFactory(tv -> {
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
    public void updateData(String matierePremiereId, MouseEvent ignoredEvent){
        this.refMatInit = matierePremiereId;
        Map<String, Object> matierePremiere = dependencyManager.getEntreeRepository().findByIds(idInit, refMatInit);
        System.out.println(matierePremiere);

        String categorie = (String) matierePremiere.get("categorie");
        String designation = (String) matierePremiere.get("designation");
        int quantite = (int) matierePremiere.get("quantite");
        String uniteMesure = (String) matierePremiere.get("uniteMesure");
        double pxUnit = (double) matierePremiere.get("px_unitaire");

        this.catMatPremBox.setValue(categorie);
        this.desMatPremBox.setValue(designation);
        this.qteMatPremField.setText(String.valueOf(quantite));
        this.refMatPremBox.setValue(matierePremiereId);
        this.uniteMesureMatPremBox.setValue(uniteMesure);
        this.pxUnitMatPremField.setText(String.valueOf(pxUnit));
    }
    public void ajouterMatierePremiere(ActionEvent ignoredEvent) throws SQLException {
        if (idInit == null) {
            idInit = referenceFacture.getText();
        } else {
            idNouveau = referenceFacture.getText();
        }

        final double devise = Double.parseDouble(valeurDevise.getText());
        final double tx_reel = Double.parseDouble(txReel.getText());

        boolean isValid = idInit != null;

        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Validation échouée");
            alert.setContentText("Veuillez remplir tous les champs avant de rajouter une entrée");
            alert.showAndWait();
            return;
        }

        if(!dependencyManager.getEntreeRepository().findByFactureId(idInit).isEmpty()) {
            dependencyManager.getEntreeRepository().update(idInit, idNouveau, tx_reel, devise);
            idInit = idNouveau;
        }

        /*
        Créer la matière première ici à partir de tous les éléments
         */
        final String categorie = catMatPremBox.getValue();
        final String reference = refMatPremBox.getValue();
        final String designation = desMatPremBox.getValue();
        final String quantite = qteMatPremField.getText();
        final String pxUnit = this.pxUnitMatPremField.getText();
        final String uniteMesure = uniteMesureMatPremBox.getValue();

        isValid = categorie != null && reference != null  && designation != null && quantite != null;

        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Validation échouée");
            alert.setContentText("Veuillez remplir tous les champs");
            alert.showAndWait();
            return;
        }

        final double pxRevientDevise = Math.round(Double.parseDouble(pxUnit) * 100.0 + (tx_reel * Double.parseDouble(pxUnit)) * 100.0)/100.0;
        final double pxRevientLocal = Math.round((Double.parseDouble(pxUnit) * devise  + tx_reel * Double.parseDouble(pxUnit) * devise)*100.0)/100.0;
        dependencyManager.getEntreeRepository().create(reference, idInit, Integer.parseInt(quantite),Double.parseDouble(pxUnit), pxRevientDevise, pxRevientLocal, categorie, designation, uniteMesure, this.img);

        Map<String, Object> ficheDeStock = dependencyManager.getFicheStockRepository().findById(reference);
        if (ficheDeStock != null){
            int qteInit = (int) ficheDeStock.get("quantite");
            int nouvelleQte = qteInit + Integer.parseInt(quantite);
            dependencyManager.getFicheStockRepository().update(reference, null, nouvelleQte, null, null);
        } else {
            dependencyManager.getFicheStockRepository().create(reference, categorie, designation, Integer.parseInt(quantite), uniteMesure);
        }

        Map<String, Object> categorieDB = dependencyManager.getCategorieRepository().findById(categorie);
        if (categorieDB == null){
            dependencyManager.getCategorieRepository().create(categorie);
        }

        Map<String, Object> designationDB = dependencyManager.getDesignationRepository().findById(designation);
        if (designationDB == null){
            dependencyManager.getDesignationRepository().create(designation);
        }

        Map<String, Object> uniteMesureDB = dependencyManager.getUniteMesureRepository().findById(uniteMesure);
        if (uniteMesureDB == null){
            dependencyManager.getUniteMesureRepository().create(uniteMesure);
        }
        updateEntreeTable();
        nbrEntree++;
    }
    public void finaliserSaisie(ActionEvent event) throws IOException, SQLException {
        idNouveau = referenceFacture.getText();
        final String fournisseur = this.fournisseurFacture.getValue();
        final String value_devise = valeurDevise.getText();
        final String tx_reel = txReel.getText();
        final String tx_theo = txTheo.getText();
        final String nom_devise = nomDevise.getValue();
        final LocalDate date = this.dateFacture.getValue();
        double tauxTheo = Double.parseDouble(tx_theo);
        double tauxReel = Double.parseDouble(tx_reel);

        final boolean validFloats = isValidFloat(value_devise) && isValidFloat(tx_reel) && isValidFloat(tx_theo);
        final boolean allFieldsFilled = idNouveau != null && fournisseur != null && value_devise != null && date != null;

        if (!validFloats) {
            showAlert("Veuillez remplir des nombres dans les champs devise, taux d'approche réel et taux d'approche théorique");
        } else if (!allFieldsFilled) {
            showAlert("Veuillez remplir tous les champs");
        } else {
            if (tauxTheo <= 0 || tauxTheo > 1) {
                showAlert("Le taux d'approche théorique doit être compris entre 0 et 1");
            } else if (tauxReel <= 0 || tauxReel > 1) {
                showAlert("Le taux d'approche réel doit être compris entre 0 et 1");
            }
        }

        assert value_devise != null;
        dependencyManager.getEntreeRepository().update(idInit, idNouveau, tauxReel, Double.parseDouble(value_devise));
        dependencyManager.getFactureRepository().create(idNouveau, date, fournisseur,tauxTheo, tauxReel, Double.parseDouble(value_devise), nom_devise ,nbrEntree);

        Map<String, Object> fournisseurDB = dependencyManager.getFournisseurRepository().findById(fournisseur);
        if (fournisseurDB == null){
            dependencyManager.getFournisseurRepository().create(fournisseur);
        }

        Map<String, Object> deviseDB = dependencyManager.getDeviseRepository().findById(nom_devise);
        if (deviseDB == null){
            dependencyManager.getDeviseRepository().create(nom_devise);
        }

        dependencyManager.getConnection().commit();
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
    public void modifier(ActionEvent ignoredEvent) throws IOException, SQLException {
        if (idInit == null) {
            idInit = referenceFacture.getText();
        } else {
            idNouveau = referenceFacture.getText();
        }

        final double devise = Double.parseDouble(valeurDevise.getText());
        final double tx_reel = Double.parseDouble(txReel.getText());

        if(!dependencyManager.getEntreeRepository().findByFactureId(idInit).isEmpty()) {
            dependencyManager.getEntreeRepository().update(idInit, idNouveau, tx_reel, devise);
            idInit = idNouveau;
        }

        final String categorie = catMatPremBox.getValue();
        final String reference = refMatPremBox.getValue();
        final String designation = desMatPremBox.getValue();
        final String quantite = qteMatPremField.getText();
        final String pxUnit = this.pxUnitMatPremField.getText();
        final String uniteMesure = uniteMesureMatPremBox.getValue();

        dependencyManager.getEntreeRepository().update(idInit, refMatInit, reference, categorie, designation, quantite,pxUnit, uniteMesure);
        dependencyManager.getFicheStockRepository().update(refMatInit, reference, Integer.parseInt(quantite), categorie, designation);
        updateEntreeTable();
    }
    @FXML
    public void supprimerMatPrem(ActionEvent ignoredE) throws SQLException {
        String idRef = refMatPremBox.getValue();
        int qte = Integer.parseInt(this.qteMatPremField.getText());
        int initQte = (int) dependencyManager.getFicheStockRepository().findById(idRef).get("quantite");
        System.out.println(qte);
        System.out.println(initQte);
        int q = initQte - qte;
        System.out.println(q);
        dependencyManager.getFicheStockRepository().update(idRef, null, q,null,null);
        dependencyManager.getEntreeRepository().delete(idRef, idInit);
        updateEntreeTable();
    }
    public void retour(ActionEvent event) throws IOException, SQLException {
        dependencyManager.getConnection().rollback();
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
    private boolean isValidFloat(String str) {
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
