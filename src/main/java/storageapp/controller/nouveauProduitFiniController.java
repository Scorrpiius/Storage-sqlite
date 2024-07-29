package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import storageapp.service.DependencyManager;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class nouveauProduitFiniController {
    private String image, matiereInit;
    @FXML
    private ImageView img;
    @FXML
    private TextField referenceProduit, qte;
    @FXML
    private ChoiceBox<String> produitChoiceBox;
    @FXML
    private ComboBox<String> matiereReferenceBox;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> matierePremiereTable;
    @FXML
    private TableColumn<Map<String, Object>, String> referenceColumn, qteColumn;
    private List<Map<String, Object>> listeMatieresPremieres;

    public nouveauProduitFiniController(DependencyManager dependencyManager){
        this.dependencyManager = dependencyManager;
        this.listeMatieresPremieres = new ArrayList<>();
    }
    public void initialize(){
        updateMatiereReferences();
        updateProduit();
    }

    public void updateMatiereReferences(){
        java.util.List<Map<String, Object>> references = dependencyManager.getFicheStockRepository().getAllId();
        List<String> referencesList = new ArrayList<>();

        for (Map<String, Object> c : references) {
            referencesList.add((String) c.get("id_matierePremiere"));
        }
        matiereReferenceBox.setItems(FXCollections.observableArrayList(referencesList));
        matiereReferenceBox.setEditable(true);
    }
    public void updateMatiereTable(){
        if (!matierePremiereTable.getItems().isEmpty()) {
            matierePremiereTable.getItems().clear();
        }

        ObservableList<Map<String, Object>> observableMatierePremieres = FXCollections.observableArrayList(listeMatieresPremieres);
        matierePremiereTable.setItems(observableMatierePremieres);

        referenceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("matiere_id")).asString());
        qteColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());

        matierePremiereTable.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    matiereInit = (String) row.getItem().get("matiere_id");
                    updateData(matiereInit, event);
                }
            });
            return row;
        });
    }
    public void updateData(String matierePremiereId, MouseEvent ignoredEvent){
        Map<String, Object> mp = listeMatieresPremieres.stream()
                .filter(map -> matierePremiereId.equals(map.get("matiere_id")))
                .findFirst()
                .get();

        matiereReferenceBox.setValue(matierePremiereId);
        qte.setText(String.valueOf(mp.get("quantite")));
    }


    public void ajouter(ActionEvent ignoredE) throws IOException {
        String referenceMatiereProduit = matiereReferenceBox.getValue();
        String quantite = qte.getText();

        boolean allFieldsFilled = !quantite.isEmpty() && referenceMatiereProduit != null;

        if(!allFieldsFilled){
            accept();
            return;
        }

        /* Vérifier que cette référence n'existe pas déjà dans la liste */
        Optional<Map<String, Object>> result = listeMatieresPremieres.stream()
                .filter(map -> referenceMatiereProduit.equals(map.get("matiere_id")))
                .findFirst();

        if(result.isPresent()){
            System.out.println("Can't add the same reference twice");
            return;
        }

        Map<String, Object> mp = new HashMap<>();
        mp.put("matiere_id", referenceMatiereProduit);
        mp.put("quantite", quantite);

        listeMatieresPremieres.add(mp);
        updateMatiereTable();
    }
    public void modifier(ActionEvent ignoredE) throws IOException {
        String newMatierePremiere = matiereReferenceBox.getValue();
        String quantite = qte.getText();

        if (matiereInit == null){
            System.out.println("Click on the line to modify");
            return;
        }

        if(!matiereInit.equals(newMatierePremiere)) {
            Optional<Map<String, Object>> result = listeMatieresPremieres.stream()
                    .filter(map -> newMatierePremiere.equals(map.get("matiere_id")))
                    .findFirst();

            if (result.isPresent()) {
                System.out.println("Already exists in the table");
                return;
            }
        }

        Map<String, Object> mp = listeMatieresPremieres.stream()
                .filter(map -> matiereInit.equals(map.get("matiere_id")))
                .findFirst()
                .get();
        mp.put("matiere_id", newMatierePremiere);
        mp.put("quantite", quantite);

        updateMatiereTable();
    }

    public void supprimerMatiere(){
        Optional<Map<String, Object>> mp = listeMatieresPremieres.stream()
                .filter(map -> matiereReferenceBox.getValue().equals(map.get("matiere_id")))
                .findFirst();
        if(mp.isEmpty()){
            System.out.println("Doesn't exist");
            return;
        } else {
            listeMatieresPremieres.remove(mp.get());
            updateMatiereTable();
        }
    }
    public void finaliserSaisie(ActionEvent e) throws SQLException, IOException {
        final String produitReference = referenceProduit.getText();
        boolean allFieldsFilled = !produitReference.isEmpty();

        if(!allFieldsFilled){
            accept();
            return;
        }
        /* Warning ID */
        if (!(dependencyManager.getProduitFiniRepository().findById(produitReference) == null)){
            showAlert();
            return;
        }

        /* Création des liens produits finis et matière première */
        for(Map<String, Object> mp  : listeMatieresPremieres){
            dependencyManager.getProduitFiniMatierePremiereRepository().create(
                    produitReference,
                    mp.get("matiere_id").toString(),
                    Integer.parseInt(mp.get("quantite").toString())
            );
        }
        dependencyManager.getProduitFiniRepository().create(produitReference, this.image);

        dependencyManager.getConnection().commit();
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }


    public void retour(ActionEvent e) throws IOException, SQLException {
        dependencyManager.getConnection().rollback();
        Node source = (Node) e.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();

    }
    public void loadImage(ActionEvent e) throws FileNotFoundException {
        Node source = (Node) e.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier PNG");

        File selectedFile = fileChooser.showOpenDialog(currentStage);
        InputStream stream = new FileInputStream(selectedFile);
        Image image = new Image(stream);
        img.setImage(image);

        this.image = String.valueOf(selectedFile);

    }

    public void updateProduit(){
        List<Map<String, Object>> produits = dependencyManager.getProduitFiniRepository().findAll();
        List<String> produitsList = new ArrayList<>();

        for (Map<String, Object> c : produits) {
            produitsList.add((String) c.get("reference"));
        }
        produitChoiceBox.setItems(FXCollections.observableArrayList(produitsList));
    }

    public void copier(ActionEvent ignoredEvent){
        /* Récupérer les liens produit finis et matières premières et les rajouter à la liste de matières premières + mettre le même id */
        String idProduit = produitChoiceBox.getValue();

        /* Récupérer les liens */
        listeMatieresPremieres = dependencyManager.getProduitFiniMatierePremiereRepository().findByProduitId(idProduit);
        referenceProduit.setText(idProduit);
        updateMatiereTable();
    }
    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Validation échouée");
        alert.setContentText("Cette référence de produit existe déjà. Veuillez saisir une nouvelle référence");
        alert.showAndWait();
    }
    private static void accept() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Validation échouée");
        alert.setContentText("Veuillez remplir tous les champs");
        alert.showAndWait();
        return;
    }

}
