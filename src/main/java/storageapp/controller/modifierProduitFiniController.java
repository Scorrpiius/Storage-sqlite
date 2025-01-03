package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.w3c.dom.Text;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class modifierProduitFiniController {
    @FXML
    private AnchorPane root;
    @FXML
    private Label titlePage;
    @FXML
    private ImageView img;
    private String matiereInit;
    private String idProduitInit;
    private final String IDPRODUIT;
    @FXML
    private TextField referenceProduit, qte;
    @FXML
    private ComboBox<String> matiereReferenceBox;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> matierePremiereTable;
    @FXML
    private TableColumn<Map<String, Object>, String> referenceColumn, qteColumn;
    public modifierProduitFiniController(DependencyManager dependencyManager, String produitId, AnchorPane root){
        this.dependencyManager = dependencyManager;
        this.idProduitInit = produitId;
        this.IDPRODUIT = produitId;
        this.root = root;
    }
    public void initialize(){
        titlePage.setText("Modifier le produit N° "+ idProduitInit);
        updateData();
        updateMatiereReferences();
        updateMatiereTable();
        matierePremiereTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    public void updateData(){
        referenceProduit.setText(idProduitInit);
    }
    public void updateMatiereReferences(){
        List<Map<String, Object>> references = dependencyManager.getFicheStockRepository().getAllId();
        List<String> referencesList = new ArrayList<>();

        for (Map<String, Object> c : references) {
            referencesList.add((String) c.get("id_matierePremiere"));
        }
        matiereReferenceBox.setItems(FXCollections.observableArrayList(referencesList));
        matiereReferenceBox.setEditable(true);
        TextFields.bindAutoCompletion(matiereReferenceBox.getEditor(), matiereReferenceBox.getItems());
    }
    public void updateMatiereTable(){
        if (!matierePremiereTable.getItems().isEmpty()) {
            matierePremiereTable.getItems().clear();
        }

        List<Map<String, Object>> matieresPremiere = dependencyManager.getProduitFiniMatierePremiereRepository().findByProduitId(idProduitInit);
        if(! (matieresPremiere == null) ){
            ObservableList<Map<String, Object>> observableMatierePremieres = FXCollections.observableArrayList(matieresPremiere);
            matierePremiereTable.setItems(observableMatierePremieres);

            referenceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("matiere_id")).asString());
            qteColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());

            matierePremiereTable.setRowFactory(tv -> {
                TableRow<Map<String, Object>> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty()) {
                        String selectMatiere = (String) row.getItem().get("matiere_id");
                        matiereInit = (String) row.getItem().get("matiere_id");
                        updateData(selectMatiere, event);
                    }
                });
                return row;
            });
        }

    }
    public void updateData(String matierePremiereId, MouseEvent ignoredEvent){
        Map<String, Object> matierePremiere = dependencyManager.getProduitFiniMatierePremiereRepository().findByIds(idProduitInit, matierePremiereId);
        int quantite = (int) matierePremiere.get("quantite");
        matiereReferenceBox.setValue(matierePremiereId);
        qte.setText(String.valueOf(quantite));
    }
    public void ajouterMatierePremiere(ActionEvent ignoredE) throws IOException {
        majId();

        String referenceMatiereProduit = matiereReferenceBox.getValue();

        /* Vérifier que la matière première choisie existe bien dans les matières premières existantes
        * et qu'elle n'existe pas déjà dans le tableau affiché */
        if (verificationRefMatPrem(referenceMatiereProduit) == 0) {
            System.out.println("Veuillez choisir une matière première qui existe ");
            return;
        } else if (verificationRefMatPrem(referenceMatiereProduit) == 1){
            System.out.println("Veuillez choisir une matière première qui n'est pas déjà rajoutée ");
            return;
        }

        String quantite = qte.getText();

        dependencyManager.getProduitFiniMatierePremiereRepository().create(idProduitInit, referenceMatiereProduit, Integer.parseInt(quantite));
        matiereReferenceBox.setValue("");
        qte.setText("");
        updateMatiereTable();    }
    public void modifierMatierePremiere(ActionEvent ignoredE) throws IOException {
        majId();

        String newMatierePremiere = matiereReferenceBox.getValue();

        if (verificationRefMatPrem(newMatierePremiere) == 0) {
            System.out.println("Veuillez choisir une matière première qui existe ");
            return;
        } else if (verificationRefMatPrem(newMatierePremiere) == 1){
            System.out.println("Veuillez choisir une matière première qui n'est pas déjà rajoutée ");
            return;
        }

        String quantite = qte.getText();
        dependencyManager.getProduitFiniMatierePremiereRepository().update(idProduitInit, matiereInit, newMatierePremiere, quantite);
        matiereReferenceBox.setValue("");
        qte.setText("");
        updateMatiereTable();
    }
    public void supprimerMatierePremiere(){
        /* Maj de l'id si il a été changé */
        majId();

        dependencyManager.getProduitFiniMatierePremiereRepository().delete(idProduitInit, matiereReferenceBox.getValue());
        updateMatiereTable();
    }
    public void finaliserModifProduitFini(ActionEvent event) throws SQLException, IOException {
        if (!IDPRODUIT.equals(referenceProduit.getText())){
            if (!(dependencyManager.getProduitFiniRepository().findById(referenceProduit.getText()) == null)){
                showAlert();
                return;
            }
        }

        boolean allFieldsFilled = !referenceProduit.getText().isEmpty();

        if(!allFieldsFilled){
            accept();
            return;
        }


        majId();
        dependencyManager.getConnection().commit();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("produitFini.fxml"));
        fxmlLoader.setController(new produitFiniController(dependencyManager, idProduitInit,  root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);
    }
    public void retourProduit(ActionEvent e) throws IOException, SQLException {
        dependencyManager.getConnection().rollback();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("produitFini.fxml"));
        fxmlLoader.setController(new produitFiniController(dependencyManager, idProduitInit,  root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);

    }
    public void telechargerImage(ActionEvent e) throws FileNotFoundException {
        Node source = (Node) e.getSource();
        Stage currentStage = (Stage) source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier PNG");

        File selectedFile = fileChooser.showOpenDialog(currentStage);
        InputStream stream = new FileInputStream(selectedFile);
        Image image = new Image(stream);
        img.setImage(image);

    }

    public void majId(){
        String idProduitNouveau = referenceProduit.getText();

        /* Si les id sont différents faire la maj sinon ne rien faire */
        if (!idProduitNouveau.equals(idProduitInit)){
            if (!(dependencyManager.getProduitFiniRepository().findById(idProduitNouveau) == null)){
                showAlert();
                return;
            }
            /* MAJ */
            dependencyManager.getProduitFiniRepository().updateProduitId(idProduitInit, idProduitNouveau);
            if (!(dependencyManager.getProduitFiniMatierePremiereRepository().findByProduitId(idProduitInit) == null)){
                dependencyManager.getProduitFiniMatierePremiereRepository().updateProduitId(idProduitInit, idProduitNouveau);
            }

            if(!(dependencyManager.getCommandeProduitFiniRepository().findByProduitId(idProduitInit) == null)){
                System.out.println(dependencyManager.getCommandeProduitFiniRepository().findByProduitId(idProduitInit));
                dependencyManager.getCommandeProduitFiniRepository().updateProduitId(idProduitInit, idProduitNouveau);
            }
        }
        idProduitInit = idProduitNouveau;
    }

    public int verificationRefMatPrem(String refAverifier){
        List<Map<String, Object>> allReferences = dependencyManager.getFicheStockRepository().getAllId();
        List<Map<String, Object>> referencesProduit = dependencyManager.getProduitFiniMatierePremiereRepository().findByProduitId(idProduitInit);

        List<String> allReferencesList = new ArrayList<>();
        List<String> produitReferencesList = new ArrayList<>();
        for (Map<String, Object> c : allReferences) {
            allReferencesList.add((String) c.get("id_matierePremiere"));
        }

        if(!(referencesProduit == null)){
            for (Map<String, Object> c : referencesProduit) {
                produitReferencesList.add((String) c.get("id_matierePremiere"));
            }
        }

        if (!allReferencesList.contains(refAverifier)) {
            return 0;
        } else if (produitReferencesList.contains(refAverifier)){
            return 1;
        }
        return 2;
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Validation échouée");
        alert.setContentText("Cette référence de bon existe déjà. Veuillez saisir une nouvelle référence");
        alert.showAndWait();
    }
    private static void accept() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Validation échouée");
        alert.setContentText("Veuillez remplir tous les champs");
        alert.showAndWait();
    }

}
