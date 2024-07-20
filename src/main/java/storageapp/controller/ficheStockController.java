package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;


import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ficheStockController {

    @FXML
    private ImageView img;
    @FXML
    private Label titlePage;
    private final String ficheStockId;
    @FXML
    private TableView<Map<String, Object>> historiqueTable;
    private final DependencyManager dependencyManager;
    @FXML
    private TextField reference, categorie, designation, uniteMesure, quantite, pxUnitMoyen, pxTotal;
    @FXML
    private TableColumn<Map<String, Object>, String> idColumn, dateColumn, pxUnitColumn, pxRevientDeviseColumn, pxRevientLocalColumn, qteColumn;

    public ficheStockController(DependencyManager dependencyManager, String ficheStockId){
        this.dependencyManager = dependencyManager;
        this.ficheStockId = ficheStockId;
    }

    public void initialize(){
        titlePage.setText("Fiche de stock - Produit " + ficheStockId);
        updateHistorique();
        updateAllData();
        updatePrix();
        loadImage();
    }

    @FXML
    public void loadImage(){
        dependencyManager.getEntreeRepository().getPicture(ficheStockId, null, img);
    }

    @FXML
    public void updatePrix(){
        List<Map<String,Object>> data = dependencyManager.getEntreeRepository().getAllInfos(ficheStockId);
        double prixUnitMoyen = 0.0;
        double prixTotal = 0.0;
        for (Map<String, Object> d : data ){
            prixUnitMoyen += Double.parseDouble(String.valueOf(d.get("px_unitaire")));
            prixTotal += Double.parseDouble(String.valueOf(d.get("px_unitaire"))) * Double.parseDouble(String.valueOf(d.get("quantite")));
        }
        prixUnitMoyen /= (long) data.size();
        this.pxUnitMoyen.setText(String.valueOf(prixUnitMoyen));
        this.pxTotal.setText(String.valueOf(prixTotal));

        this.pxTotal.setEditable(false);
        this.pxUnitMoyen.setEditable(false);
    }

    public void updateHistorique(){
        List<Map<String,Object>> historiqueData = dependencyManager.getEntreeRepository().getAllInfos(ficheStockId);

        if (!historiqueTable.getItems().isEmpty()) {
            historiqueTable.getItems().clear();
        }

        ObservableList<Map<String, Object>> observableHistorique = FXCollections.observableArrayList(historiqueData);
        historiqueTable.setItems(observableHistorique);

        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_facture")).asString());
        dateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("date")).asString());
        pxUnitColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("px_unitaire")).asString());
        pxRevientDeviseColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("px_revient_devise")).asString());
        pxRevientLocalColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("px_revient_local")).asString());
        qteColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());
    }

    public void updateAllData(){
        Map<String, Object> ficheStock = dependencyManager.getFicheStockRepository().findById(ficheStockId);
        String categorie = (String) ficheStock.get("categorie");
        String designation = (String) ficheStock.get("designation");
        String uniteMesure = (String) ficheStock.get("uniteMesure");
        int quantite = (int) ficheStock.get("quantite");

        this.categorie.setText(categorie);
        this.designation.setText(designation);
        this.quantite.setText(String.valueOf(quantite));
        this.reference.setText(ficheStockId);
        this.uniteMesure.setText(uniteMesure);

        this.categorie.setEditable(false);
        this.designation.setEditable(false);
        this.quantite.setEditable(false);
        this.reference.setEditable(false);
        this.uniteMesure.setEditable(false);
    }

    public void retour(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }
}
