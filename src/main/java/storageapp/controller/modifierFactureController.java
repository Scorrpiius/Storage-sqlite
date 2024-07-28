package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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

public class modifierFactureController {
    @FXML
    private Label titlePage;
    @FXML
    private ImageView matiereImg;
    private String idFactureInit;
    private final String IDFACTURE;
    private String img, refMatInit;
    @FXML
    private DatePicker dateFacturePicker;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> matierePremiereTable;
    @FXML
    private TextField referenceFactureField, valeurDeviseField, tauxTheoField, tauxReelField, qteMatPremField, pxUnitMatPremField;
    @FXML
    private ComboBox<String> nomDeviseBox, fournisseurFactureBox, refMatPremBox, catMatPremBox, desMatPremBox, uniteMesureMatPremBox;
    @FXML
    private TableColumn<Map<String, Object>, String> refMatPremCol, desMatPremCol, catMatPremCol, qteMatPremCol, pxUnitMatPremCol, pxRevientDeviseMatPremColumn, pxRevientLocalMatPremColumn;

    public modifierFactureController(DependencyManager dependencyManager, String factureId){
        this.dependencyManager = dependencyManager;
        this.idFactureInit = factureId;
        this.IDFACTURE = factureId;
    }
    public void initialize(){
        titlePage.setText("Facture N° " + idFactureInit);
        updateAllData();
        updateEntreeTable();
        updateDevise();
        updateFournisseur();
        updateCategorie();
        updateDesignation();
        updateUniteMesure();
        updateReference();
    }
    public void updateAllData(){
        Map<String, Object> facture = dependencyManager.getFactureRepository().findById(idFactureInit);
        String fournisseur = (String) facture.get("fournisseur");
        String date = (String) facture.get("date");
        String deviseValue = String.valueOf(facture.get("valeur_devise"));
        String deviseName = (String) facture.get("nom_devise");
        String txReel = String.valueOf(facture.get("tx_reel"));
        String txTheo = String.valueOf(facture.get("tx_theo"));
        pxRevientDeviseMatPremColumn.setText("Prix de revient \n("+deviseName+")");

        referenceFactureField.setText(idFactureInit);
        this.fournisseurFactureBox.setValue(fournisseur);
        valeurDeviseField.setText(deviseValue);
        nomDeviseBox.setValue(deviseName);
        this.tauxTheoField.setText(txTheo);
        this.tauxReelField.setText(txReel);
        this.dateFacturePicker.setValue(LocalDate.parse(date));
    }
    public void updateFournisseur(){
        List<Map<String, Object>> fournisseurs = dependencyManager.getFournisseurRepository().findAll();
        List<String> fournisseursList = new ArrayList<>();

        for(Map<String, Object> c : fournisseurs){
            fournisseursList.add((String)c.get("nom"));
        }
        fournisseurFactureBox.setItems(FXCollections.observableArrayList(fournisseursList));
        fournisseurFactureBox.setEditable(true);
    }
    public void updateUniteMesure(){
        List<Map<String, Object>> uniteMesures = dependencyManager.getUniteMesureRepository().findAll();
        List<String> uniteMesuresList = new ArrayList<>();
        for(Map<String, Object> c : uniteMesures){
            uniteMesuresList.add((String)c.get("nom"));
        }
        uniteMesureMatPremBox.setItems(FXCollections.observableArrayList(uniteMesuresList));
        uniteMesureMatPremBox.setEditable(true);
    }
    public void updateReference(){
        List<Map<String, Object>> references = dependencyManager.getFicheStockRepository().getAllId();
        List<String> referencesList = new ArrayList<>();

        for (Map<String, Object> c : references) {
            referencesList.add((String) c.get("id_matierePremiere"));
        }
        refMatPremBox.setItems(FXCollections.observableArrayList(referencesList));
        refMatPremBox.setEditable(true);
    }
    public void updateDesignation(){
        List<Map<String, Object>> designations = dependencyManager.getDesignationRepository().findAll();
        List<String> designationsList = new ArrayList<>();

        for(Map<String, Object> c : designations){
            designationsList.add((String)c.get("nom"));
        }
        desMatPremBox.setItems(FXCollections.observableArrayList(designationsList));
        desMatPremBox.setEditable(true);
    }
    public void updateCategorie(){
        List<Map<String, Object>> categories = dependencyManager.getCategorieRepository().findAll();
        List<String> categoriesList = new ArrayList<>();

        for(Map<String, Object> c : categories){
            categoriesList.add((String)c.get("nom"));
        }
        catMatPremBox.setItems(FXCollections.observableArrayList(categoriesList));
        catMatPremBox.setEditable(true);
    }
    public void updateDevise(){
        List<Map<String, Object>> devises = dependencyManager.getDeviseRepository().findAll();
        List<String> devisesList = new ArrayList<>();

        for(Map<String, Object> c : devises){
            devisesList.add((String)c.get("nom"));
        }
        nomDeviseBox.setItems(FXCollections.observableArrayList(devisesList));
        nomDeviseBox.setEditable(true);
    }
    public void updateEntreeTable(){
        if (!matierePremiereTable.getItems().isEmpty()) {
            matierePremiereTable.getItems().clear();
        }

        final String nom_devise = nomDeviseBox.getValue();
        pxRevientDeviseMatPremColumn.setText("Prix de revient \n("+nom_devise+")");

        List<Map<String, Object>> entrees = dependencyManager.getEntreeRepository().findByFactureId(idFactureInit);
        ObservableList<Map<String, Object>> observableEntrees = FXCollections.observableArrayList(entrees);
        matierePremiereTable.setItems(observableEntrees);

        double devise = Double.parseDouble(valeurDeviseField.getText());
        double tx_reel = Double.parseDouble(tauxReelField.getText());

        refMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_reference")).asString());
        desMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("designation")).asString());
        catMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("categorie")).asString());
        qteMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());
        pxUnitMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("px_unitaire")).asString());
        pxRevientDeviseMatPremColumn.setCellValueFactory(cellData -> {
            Object pxUnitaireObj = cellData.getValue().get("px_unitaire");
            double pxUnitaire = pxUnitaireObj != null ? Double.parseDouble(pxUnitaireObj.toString()) : 0;

            double result = pxUnitaire + tx_reel * pxUnitaire;
            return new SimpleObjectProperty<>(String.format("%.2f", result));

        });
        pxRevientLocalMatPremColumn.setCellValueFactory(cellData -> {
            Object pxUnitaireObj = cellData.getValue().get("px_unitaire");
            double pxUnitaire = pxUnitaireObj != null ? Double.parseDouble(pxUnitaireObj.toString()) : 0;

            double result = pxUnitaire * devise + tx_reel * pxUnitaire * devise;
            return new SimpleObjectProperty<>(String.format("%.2f", result));

        });

        matierePremiereTable.setRowFactory(tv -> {
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
        Map<String, Object> matierePremiere = dependencyManager.getEntreeRepository().findByIds(idFactureInit, refMatInit);

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
    public void ajouterMatierePremiere(ActionEvent ignoredevent) throws SQLException {
        /* Maj de l'id de facture si elle a été faite */
        majId();
        majPrix();

        String categorie = catMatPremBox.getValue();
        String reference = refMatPremBox.getValue();
        String designation = desMatPremBox.getValue();
        String quantite = qteMatPremField.getText();
        String pxUnit = this.pxUnitMatPremField.getText();
        String uniteMesure = uniteMesureMatPremBox.getValue();

        boolean isValid = categorie != null && reference != null  && designation != null && quantite != null;

        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Validation échouée");
            alert.setContentText("Veuillez remplir tous les champs");
            alert.showAndWait();
            return;
        }

        dependencyManager.getEntreeRepository().create(reference, idFactureInit, Integer.parseInt(quantite),Double.parseDouble(pxUnit),0.0 , 0.0, categorie, designation, uniteMesure, this.img);
        majPrix();

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
    public void modifier() throws IOException, SQLException {
        /* Maj si l'id facture a été modifié et des prix si modifications de la devise  */
        majId();
        majPrix();

        /* Modififcation de la matière première et de la fiche de stock */
        String categorie = catMatPremBox.getValue();
        String reference = refMatPremBox.getValue();
        String designation = desMatPremBox.getValue();
        String quantite = qteMatPremField.getText();
        String pxUnit = this.pxUnitMatPremField.getText();
        String uniteMesure = uniteMesureMatPremBox.getValue();
        int quantiteInit, quantiteEnStock, nouvelleQteStock;
        int nouvelleQuantite = Integer.parseInt(quantite);


        if (refMatInit == null){
            refMatInit = reference;
        }

        quantiteInit =  Integer.parseInt(dependencyManager.getEntreeRepository().findByIds(idFactureInit, refMatInit).get("quantite").toString());
        quantiteEnStock = Integer.parseInt(dependencyManager.getFicheStockRepository().findById(refMatInit).get("quantite").toString());
        nouvelleQteStock= quantiteEnStock - quantiteInit + nouvelleQuantite;
        dependencyManager.getEntreeRepository().update(idFactureInit, refMatInit, reference, categorie, designation, quantite, pxUnit, uniteMesure);
        dependencyManager.getFicheStockRepository().update(refMatInit, reference, nouvelleQteStock ,categorie, designation);

        updateEntreeTable();
    }
    public void supprimerMatPrem(ActionEvent ignoredE) throws SQLException {
        /* Faire la maje de l'id de facture si il y a eu des modifications + la maj des prix */
        majId();
        majPrix();

        /* Supprimer la matiere première de la base de donnée + mettre à jour la fiche de stock en supprimer la quantité associée */
        String refToDelete = refMatPremBox.getValue();
        int qteToRemove = Integer.parseInt(qteMatPremField.getText());
        int qteEnStock = Integer.parseInt(dependencyManager.getFicheStockRepository().findById(refToDelete).get("quantite").toString());
        int nouvelleQte = qteEnStock - qteToRemove;

        dependencyManager.getFicheStockRepository().update(refToDelete, null, nouvelleQte, null, null);
        dependencyManager.getEntreeRepository().delete(refToDelete, idFactureInit);
        updateEntreeTable();
    }
    public void finir(ActionEvent event) throws SQLException {
        if(!IDFACTURE.equals(referenceFactureField.getText())){
            if (!(dependencyManager.getFactureRepository().findById(referenceFactureField.getText()) == null)){
                showAlert("Cette référence de facture existe déjà. Veuillez saisir une nouvelle référence");
                return;
            }
        }

        majId();
        majPrix();

        final String fournisseur = this.fournisseurFactureBox.getValue();
        final String deviseValue = valeurDeviseField.getText();
        final String deviseName = nomDeviseBox.getValue();
        final String txTheo = this.tauxTheoField.getText();
        final String txReel = this.tauxReelField.getText();
        final LocalDate date = this.dateFacturePicker.getValue();
        double tauxTheo = Double.parseDouble(txTheo);
        double tauxReel = Double.parseDouble(txReel);

        final boolean validFloats = isValidFloat(deviseValue) && isValidFloat(txReel) && isValidFloat(txTheo);
        final boolean allFieldsFilled = fournisseur != null && deviseValue != null && date != null;

        if (!validFloats) {
            showAlert("Veuillez remplir des nombres dans les champs devise, taux d'approche réel et taux d'approche théorique");
        } else if (!allFieldsFilled) {
            showAlert("Veuillez remplir tous les champs");
        } else {
            // Check if tauxTheo and tauxReel are within valid range
            if (tauxTheo <= 0 || tauxTheo > 1) {
                showAlert("Le taux d'approche théorique doit être compris entre 0 et 1");
            } else if (tauxReel <= 0 || tauxReel > 1) {
                showAlert("Le taux d'approche réel doit être compris entre 0 et 1");
            }
        }

        assert deviseValue != null;
        int nbrEntree = dependencyManager.getEntreeRepository().findByFactureId(idFactureInit).size();
        dependencyManager.getFactureRepository().updateOtherInfo(idFactureInit, date, fournisseur,tauxTheo, tauxReel, Double.parseDouble(deviseValue), deviseName ,nbrEntree);

        dependencyManager.getConnection().commit();
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
    public void retour(ActionEvent event) throws SQLException, IOException {
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
    /* Met à jour la référence de la facture pour la facture et les différentes matières premières associés si il y a eu des estrangements */
    public void majId(){
        String idFactureNouveau = referenceFactureField.getText();

        /* Si les id sont différents faire la maj sinon ne rien faire */
        if (!idFactureNouveau.equals(idFactureInit)){

            /* MAJ */
            dependencyManager.getEntreeRepository().updateFactureId(idFactureInit, idFactureNouveau);
            dependencyManager.getFactureRepository().updateFactureId(idFactureInit, idFactureNouveau);
        }
        idFactureInit = idFactureNouveau;
    }
    /* Met à jour les valeurs des prix de revient pour les matières premières */
    public void majPrix(){

        double valeurDevise = Double.parseDouble(this.valeurDeviseField.getText());
        double tauxReel = Double.parseDouble(this.tauxReelField.getText());

        dependencyManager.getEntreeRepository().updatePrix(idFactureInit, valeurDevise, tauxReel);
    }
}
