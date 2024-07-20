package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class nouveauProduitFiniController {
    private String image, matiereInit;
    @FXML
    private ImageView img;
    @FXML
    private TextField reference, qte;
    @FXML
    private ComboBox<String> matiereReferenceBox;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> matierePremiereTable;
    @FXML
    private TableColumn<Map<String, Object>, String> referenceColumn, qteColumn;

    public nouveauProduitFiniController(DependencyManager dependencyManager){this.dependencyManager = dependencyManager;}
    public void initialize(){
        updateMatiereReferences();
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

    public void retour(ActionEvent e) throws IOException, SQLException {
        dependencyManager.getConnection().rollback();
        Node source = (Node) e.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();

    }

    public void finaliserSaisie(ActionEvent e) throws SQLException, IOException {
        final String produitReference = reference.getText();
        dependencyManager.getProduitFiniRepository().create(produitReference, this.image);

        dependencyManager.getConnection().commit();
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void modifier(ActionEvent e) throws IOException {
        String newMatierePremiere = matiereReferenceBox.getValue();
        String quantite = qte.getText();
        dependencyManager.getProduitFiniMatierePremiereRepository().update(reference.getText(), matiereInit, newMatierePremiere, quantite);
        updateMatiereTable();
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

    public void ajouter(ActionEvent e) throws IOException {
        String referenceProduit = reference.getText();
        String referenceMatiereProduit = matiereReferenceBox.getValue();
        String quantite = qte.getText();
        // Si la matiere premiere est deja associe a un produit fini alors update la quantite sinon cree la nouvelle ref
        if(dependencyManager.getProduitFiniMatierePremiereRepository().findByIds(referenceProduit, referenceMatiereProduit) != null){
            dependencyManager.getProduitFiniMatierePremiereRepository().updateQuantite(referenceProduit, referenceMatiereProduit, Integer.parseInt(quantite));
        } else {
            dependencyManager.getProduitFiniMatierePremiereRepository().create(referenceProduit, referenceMatiereProduit, Integer.parseInt(quantite));
        }
        updateMatiereTable();
    }

    public void updateMatiereTable(){
        if (!matierePremiereTable.getItems().isEmpty()) {
            matierePremiereTable.getItems().clear();
        }
        String referenceProduit = reference.getText();

        List<Map<String, Object>> matieresPremiere = dependencyManager.getProduitFiniMatierePremiereRepository().findByProduitId(referenceProduit);
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

    public void updateData(String matierePremiereId, MouseEvent ignoredEvent){
        Map<String, Object> matierePremiere = dependencyManager.getProduitFiniMatierePremiereRepository().findByIds(reference.getText(), matierePremiereId);
        int quantite = (int) matierePremiere.get("quantite");
        matiereReferenceBox.setValue(matierePremiereId);
        qte.setText(String.valueOf(quantite));
    }

    public void supprimerMatiere(){
        dependencyManager.getProduitFiniMatierePremiereRepository().delete(reference.getText(), matiereReferenceBox.getValue());
        updateMatiereTable();
    }
}
