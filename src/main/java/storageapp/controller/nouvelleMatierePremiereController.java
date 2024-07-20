package storageapp.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import storageapp.service.DependencyManager;


import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class nouvelleMatierePremiereController {
    private String img;
    @FXML
    private ImageView matiereImg;
    private final String factureId;
    private final double devise, tx_reel;
    @FXML
    private TextField qte, pxUnit;
    private final DependencyManager dependencyManager;
    @FXML
    private ComboBox<String> categorieBox, referenceBox, designationBox, uniteMesureBox;

    public nouvelleMatierePremiereController(DependencyManager dependencyManager, String factureId, String devise, String tx_reel){
        this.dependencyManager = dependencyManager;
        this.factureId = factureId;
        this.devise = Double.parseDouble(devise);
        this.tx_reel = Double.parseDouble(tx_reel);
    }

    public void initialize(){
        updateCategorie();
        updateDesignation();
        updateReference();
        updateUniteMesure();
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
    public void updateReference(){
        List<Map<String, Object>> references = dependencyManager.getFicheStockRepository().getAllId();
        List<String> referencesList = new ArrayList<>();

        for (Map<String, Object> c : references) {
            referencesList.add((String) c.get("id_matierePremiere"));
        }
        referenceBox.setItems(FXCollections.observableArrayList(referencesList));
        referenceBox.setEditable(true);
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

    public void finirSaisie(ActionEvent event) throws SQLException {
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

        final double pxRevientDevise = Math.round((Double.parseDouble(pxUnit)  + tx_reel * Double.parseDouble(pxUnit))*100.0)/100.0;
        final double pxRevientLocal = Math.round((Double.parseDouble(pxUnit) * devise  + tx_reel * Double.parseDouble(pxUnit) * devise)*100.0)/100.0;
        dependencyManager.getEntreeRepository().create(reference, factureId, Integer.parseInt(quantite),Double.parseDouble(pxUnit), pxRevientDevise, pxRevientLocal, categorie, designation, uniteMesure, this.img);

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

        Node source = (Node) event.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();
        currentStage.close();
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
}
