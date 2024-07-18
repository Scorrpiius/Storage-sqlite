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


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class nouvelleEntreeController {
    @FXML
    private TextField qteField, pxUnitField;
    @FXML
    private ComboBox<String> categorieField, referenceField, designationField, unitMesureField;
    @FXML
    private ImageView matiereImg;
    private final DependencyManager dependencyManager;
    private final String factureId;
    private final double devise, tx_reel;
    private String img;

    public nouvelleEntreeController(DependencyManager dependencyManager,String factureId, String devise, String tx_reel){
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
        categorieField.setItems(FXCollections.observableArrayList(categoriesList));
        categorieField.setEditable(true);
    }
    @FXML
    public void updateDesignation(){
        List<Map<String, Object>> designations = dependencyManager.getDesignationRepository().findAll();
        List<String> designationsList = new ArrayList<>();

        for(Map<String, Object> c : designations){
            designationsList.add((String)c.get("nom"));
        }
        designationField.setItems(FXCollections.observableArrayList(designationsList));
        designationField.setEditable(true);
    }
    @FXML
    public void updateReference(){
        List<Map<String, Object>> references = dependencyManager.getFicheStockRepository().getAllId();
        List<String> referencesList = new ArrayList<>();
        referenceField.setEditable(true);
        if (!referencesList.isEmpty()){
            for(Map<String, Object> c : references){
                referencesList.add((String)c.get("id_matierePremiere"));
            }
            referenceField.setItems(FXCollections.observableArrayList(referencesList));
        }
    }
    @FXML
    public void updateUniteMesure(){
        List<Map<String, Object>> uniteMesures = dependencyManager.getFicheStockRepository().getAllId();
        List<String> uniteMesuresList = new ArrayList<>();
        unitMesureField.setEditable(true);
        if (!uniteMesuresList.isEmpty()){
            for(Map<String, Object> c : uniteMesures){
                uniteMesuresList.add((String)c.get("nom"));
            }
            unitMesureField.setItems(FXCollections.observableArrayList(uniteMesuresList));
        }
    }

    public void finirSaisie(ActionEvent event) throws IOException, SQLException {
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
        // Cree une matière première et l'ajoute à la base de donnée
        final double pxRevient = Math.round((Double.parseDouble(pxUnit) * devise + tx_reel * Double.parseDouble(pxUnit) * devise)*100.0)/100.0;
        dependencyManager.getEntreeRepository().create(reference,null,Integer.parseInt(quantite), Double.parseDouble(pxUnit), pxRevient,categorie,designation,uniteMesure, this.img);

        // Crée une fiche de stock à partir de la matière première si elle n'existe pas déja, si elle existe on met à jour la quantité
        Map<String, Object> ficheDeStock = dependencyManager.getFicheStockRepository().findById(reference);
        if (ficheDeStock != null){
            int qteInit = (int) ficheDeStock.get("quantite");
            int nouvelleQte = qteInit + Integer.parseInt(quantite);
            dependencyManager.getFicheStockRepository().update(reference, null, nouvelleQte, null, null);
        } else {
            dependencyManager.getFicheStockRepository().create(reference, categorie, designation, Integer.parseInt(quantite), uniteMesure);
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

        // Show the dialog and wait for the user to select a file
        File selectedFile = fileChooser.showOpenDialog(currentStage);
        InputStream stream = new FileInputStream(selectedFile);
        Image image = new Image(stream);
        //Creating the image view
        //Setting image to the image view
        matiereImg.setImage(image);

        this.img = String.valueOf(selectedFile);
    }
}
