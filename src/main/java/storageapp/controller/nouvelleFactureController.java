
package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import java.util.*;
import java.text.DecimalFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;


public class nouvelleFactureController {

    private int nbrEntree;
    @FXML
    private ImageView matiereImg;
    private String img, refMatInit;
    @FXML
    private DatePicker dateFacturePicker;
    List<Map<String, Object>> listMatierePremiere;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> matierePremiereTable;
    @FXML
    private TextField referenceFactureField, valeurDeviseField, tauxTheoField, tauxReelField, qteMatPremField, pxUnitMatPremField;
    @FXML
    private ComboBox<String> nomDeviseBox, fournisseurFactureBox, refMatPremBox, catMatPremBox, desMatPremBox, uniteMesureMatPremBox;
    @FXML
    private TableColumn<Map<String, Object>, String> refMatPremCol, desMatPremCol, catMatPremCol, qteMatPremCol, pxUnitMatPremCol, pxRevientDeviseMatPremColumn, pxRevientLocalMatPremColumn;

    public nouvelleFactureController(DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
        this.nbrEntree = 0;
        this.listMatierePremiere = new ArrayList<>();
    }
    private static void accept() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Validation échouée");
        alert.setContentText("Veuillez remplir tous les champs");
        alert.showAndWait();
        return;
    }
    public void initialize(){
        updateDevise();
        updateFournisseur();
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
        catMatPremBox.setItems(FXCollections.observableArrayList(categoriesList));
        catMatPremBox.setEditable(true);

        autoCompletion(catMatPremBox, categoriesList);
    }
    public void updateDesignation(){
        List<Map<String, Object>> designations = dependencyManager.getDesignationRepository().findAll();
        List<String> designationsList = new ArrayList<>();

        for(Map<String, Object> c : designations){
            designationsList.add((String)c.get("nom"));
        }
        desMatPremBox.setItems(FXCollections.observableArrayList(designationsList));
        desMatPremBox.setEditable(true);

        autoCompletion(desMatPremBox, designationsList);
    }
    public void updateReference(){
        List<Map<String, Object>> references = dependencyManager.getFicheStockRepository().getAllId();
        List<String> referencesList = new ArrayList<>();

        for (Map<String, Object> c : references) {
            referencesList.add((String) c.get("id_matierePremiere"));
        }
        for (Map<String, Object> mp : listMatierePremiere){
            if (!referencesList.contains((String) mp.get("id_reference"))){
                referencesList.add((String)mp.get("id_reference"));
            }
        }
        refMatPremBox.setItems(FXCollections.observableArrayList(referencesList));
        refMatPremBox.setEditable(true);

        autoCompletion(refMatPremBox, referencesList);

    }

    public void autoCompletion(ComboBox<String> comboBox, List<String> referencesList ){
        TextField textField = comboBox.getEditor();
        FilteredList<String> filteredItems = new FilteredList<>(FXCollections.observableArrayList(referencesList), p -> true);

        textField.textProperty().addListener((obs, oldValue, newValue) -> {
            final TextField editor = comboBox.getEditor();
            final String selected = comboBox.getSelectionModel().getSelectedItem();

            if (selected == null || !selected.equals(editor.getText())) {
                filterItems(filteredItems, newValue, comboBox);
                comboBox.show();
            }
        });
        comboBox.setItems(filteredItems);
    }
    private void filterItems(FilteredList<String> filteredItems, String filter, ComboBox<String> comboBox) {
        filteredItems.setPredicate(item -> {
            if (filter == null || filter.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = filter.toLowerCase();
            return item.toLowerCase().contains(lowerCaseFilter);
        });
    }

    public void updateUniteMesure(){
        List<Map<String, Object>> uniteMesures = dependencyManager.getUniteMesureRepository().findAll();
        List<String> uniteMesuresList = new ArrayList<>();
        for(Map<String, Object> c : uniteMesures){
            uniteMesuresList.add((String)c.get("nom"));
        }
        uniteMesureMatPremBox.setItems(FXCollections.observableArrayList(uniteMesuresList));
        uniteMesureMatPremBox.setEditable(true);

        autoCompletion(uniteMesureMatPremBox, uniteMesuresList);
    }
    public void updateFournisseur(){
        List<Map<String, Object>> fournisseurs = dependencyManager.getFournisseurRepository().findAll();
        List<String> fournisseursList = new ArrayList<>();

        for(Map<String, Object> c : fournisseurs){
            fournisseursList.add((String)c.get("nom"));
        }
        fournisseurFactureBox.setItems(FXCollections.observableArrayList(fournisseursList));
        fournisseurFactureBox.setEditable(true);

        autoCompletion(fournisseurFactureBox, fournisseursList);
    }
    public void updateDevise(){
        List<Map<String, Object>> devises = dependencyManager.getDeviseRepository().findAll();
        List<String> devisesList = new ArrayList<>();

        for(Map<String, Object> c : devises){
            devisesList.add((String)c.get("nom"));
        }
        nomDeviseBox.setItems(FXCollections.observableArrayList(devisesList));
        nomDeviseBox.setEditable(true);

        autoCompletion(nomDeviseBox, devisesList);
    }
    public void updateEntreeTable(){
        if (!matierePremiereTable.getItems().isEmpty()) {
            matierePremiereTable.getItems().clear();
        }

        final String nom_devise = nomDeviseBox.getValue();
        pxRevientDeviseMatPremColumn.setText("Prix de revient \n("+nom_devise+")");

        double devise = Double.parseDouble(valeurDeviseField.getText());
        double tx_reel = Double.parseDouble(tauxReelField.getText());

        ObservableList<Map<String, Object>> observableEntrees = FXCollections.observableArrayList(listMatierePremiere);
        matierePremiereTable.setItems(observableEntrees);

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
        Map<String, Object> matierePremiere = listMatierePremiere.stream()
                .filter(map -> matierePremiereId.equals(map.get("id_reference")))
                .findFirst()
                .get();
        String categorie = (String) matierePremiere.get("categorie");
        String designation = (String) matierePremiere.get("designation");
        int quantite = Integer.parseInt((String)matierePremiere.get("quantite"));
        String uniteMesure = (String) matierePremiere.get("uniteMesure");
        double pxUnit = Double.parseDouble((String)matierePremiere.get("px_unitaire"));

        this.catMatPremBox.setValue(categorie);
        this.desMatPremBox.setValue(designation);
        this.qteMatPremField.setText(String.valueOf(quantite));
        this.refMatPremBox.setValue(matierePremiereId);
        this.uniteMesureMatPremBox.setValue(uniteMesure);
        this.pxUnitMatPremField.setText(String.valueOf(pxUnit));
    }
    public void ajouterMatierePremiere(ActionEvent ignoredEvent) throws SQLException {
        /* Ajouter la matière première créée à la liste */

        /* Créer la matière première ici à partir de tous les éléments */
        final String categorie = catMatPremBox.getValue();
        final String reference = refMatPremBox.getValue();
        final String designation = desMatPremBox.getValue();
        final String quantite = qteMatPremField.getText();
        final String pxUnit = pxUnitMatPremField.getText();
        final String uniteMesure = uniteMesureMatPremBox.getValue();
        final String valeurDevise = valeurDeviseField.getText();
        final String tauxReel = tauxReelField.getText();

        /* Vérifier que tous les éléments nécessaire ont été ajouté */
        boolean allFieldsFilled = categorie != null
                && reference != null && designation != null
                && !quantite.isEmpty() && !pxUnit.isEmpty()
                && uniteMesure != null && !valeurDevise.isEmpty()
                && !tauxReel.isEmpty();

        if(!allFieldsFilled){
            accept();
            return;
        }

        /* Vérifier que la quantité est bien un entier */
        if (!quantite.matches("-?\\d+")){
            showAlert("La quantité doit être un nombre entier");
            return;
        }

        /* Vérifier que cette référence n'existe pas déjà dans la liste */
        Optional<Map<String, Object>> result = listMatierePremiere.stream()
                .filter(map -> reference.equals(map.get("id_reference")))
                .findFirst();

        if(result.isPresent()){
            System.out.println("Can't add the same reference twice");
            return;
        }

        Map<String, Object> mp = new HashMap<>();
        mp.put("id_reference", reference);
        mp.put("quantite", quantite);
        mp.put("px_unitaire", pxUnit);
        mp.put("categorie", categorie);
        mp.put("designation", designation);
        mp.put("uniteMesure", uniteMesure);
        listMatierePremiere.add(mp);

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

        uniteMesureMatPremBox.setValue("");
        refMatPremBox.setValue("");
        catMatPremBox.setValue("");
        desMatPremBox.setValue("");
        pxUnitMatPremField.setText("");
        qteMatPremField.setText("");

        updateEntreeTable();
        updateCategorie();
        updateDesignation();
        updateReference();
        updateUniteMesure();
        nbrEntree++;
    }
    public void modifier(ActionEvent ignoredEvent) throws IOException, SQLException {
        final String categorie = catMatPremBox.getValue();
        final String reference = refMatPremBox.getValue();
        final String designation = desMatPremBox.getValue();
        final String quantite = qteMatPremField.getText();
        final String pxUnit = pxUnitMatPremField.getText();
        final String uniteMesure = uniteMesureMatPremBox.getValue();

        if(!refMatInit.equals(reference)){
            Optional<Map<String, Object>> result = listMatierePremiere.stream()
                    .filter(map -> reference.equals(map.get("id_reference")))
                    .findFirst();

            if(result.isPresent()){
                System.out.println("This reference already exists in the table, can't add it twice");
                return;
            }
        }

        /* Modifier les infos dans la liste */
        Map<String, Object> mp = listMatierePremiere.stream()
                .filter(map -> refMatInit.equals(map.get("id_reference")))
                .findFirst()
                .get();

        mp.put("id_reference", reference);
        mp.put("categorie", categorie);
        mp.put("designation", designation);
        mp.put("quantite", quantite);
        mp.put("px_unitaire", pxUnit);
        mp.put("uniteMesure", uniteMesure);

        uniteMesureMatPremBox.setValue("");
        refMatPremBox.setValue("");
        catMatPremBox.setValue("");
        desMatPremBox.setValue("");
        pxUnitMatPremField.setText("");
        qteMatPremField.setText("");

        updateEntreeTable();
        updateCategorie();
        updateDesignation();
        updateReference();
        updateUniteMesure();
    }
    public void supprimerMatPrem(ActionEvent ignoredE) {
        /* Récupérer la position de la matière première à supprimer (créer une méthode qui report l'index de l'élément recherché) */
        String refToDelete = refMatPremBox.getValue();
        Map<String, Object> mp = listMatierePremiere.stream()
                .filter(map -> refToDelete.equals(map.get("id_reference")))
                .findFirst()
                .get();
        listMatierePremiere.remove(mp);
        updateEntreeTable();
    }
    public void finaliserSaisie(ActionEvent event) throws IOException, SQLException {
        final String idFacture = referenceFactureField.getText();
        final String fournisseur = this.fournisseurFactureBox.getValue();
        final String value_devise = valeurDeviseField.getText();
        final String tx_reel = tauxReelField.getText();
        final String tx_theo = tauxTheoField.getText();
        final String nom_devise = nomDeviseBox.getValue();
        final LocalDate date = this.dateFacturePicker.getValue();

        double tauxTheo = Double.parseDouble(tx_theo);
        double tauxReel = Double.parseDouble(tx_reel);
        double devise = Double.parseDouble(valeurDeviseField.getText());

        boolean allFieldsFilled = !idFacture.isEmpty()
                && fournisseur != null && nom_devise != null
                && !tx_reel.isEmpty() && date != null
                && !value_devise.isEmpty() && !tx_theo.isEmpty();

        if(!allFieldsFilled){
            accept();
            return;
        }

        if (!(dependencyManager.getFactureRepository().findById(idFacture) == null)){
            showAlert("Cette référence de facture existe déjà. Veuillez saisir une nouvelle référence");
            return;
        }


        final boolean validFloats = isValidFloat(value_devise) && isValidFloat(tx_reel) && isValidFloat(tx_theo);

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

        /* Création de toutes les matières premières et les fiches de stock corrspondantes */
        for(Map<String, Object> mp : listMatierePremiere){
            double pxUnit = Double.parseDouble(mp.get("px_unitaire").toString());
            /* Matière première */
            dependencyManager.getEntreeRepository().create(mp.get("id_reference").toString(),
                    idFacture,
                    Integer.parseInt(mp.get("quantite").toString()),
                    pxUnit,
                    pxUnit + (pxUnit * tauxReel),
                    (pxUnit * devise) + tauxReel * pxUnit * devise,
                    mp.get("categorie").toString(),
                    mp.get("designation").toString(),
                    mp.get("uniteMesure").toString(),
                    this.img
            );

            /* Fiche de stock */
            Map<String, Object> ficheDeStock = dependencyManager.getFicheStockRepository().findById(mp.get("id_reference").toString());
            if (ficheDeStock != null){
                int qteInit = (int) ficheDeStock.get("quantite");
                int nouvelleQte = qteInit + Integer.parseInt(mp.get("quantite").toString());
                dependencyManager.getFicheStockRepository().update(
                        mp.get("id_reference").toString(),
                        null,
                        nouvelleQte,
                        null,
                        null
                );
            } else {
                dependencyManager.getFicheStockRepository().create(
                        mp.get("id_reference").toString(),
                        mp.get("categorie").toString(),
                        mp.get("designation").toString(),
                        Integer.parseInt(mp.get("quantite").toString()),
                        mp.get("uniteMesure").toString()
                );
            }
        }

        dependencyManager.getFactureRepository().create(
                idFacture,
                date,
                fournisseur,
                tauxTheo,
                tauxReel,
                Double.parseDouble(value_devise),
                nom_devise,
                nbrEntree);

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
