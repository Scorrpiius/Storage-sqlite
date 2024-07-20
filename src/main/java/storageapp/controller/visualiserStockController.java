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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class visualiserStockController {
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> ficheStockTable;
    @FXML
    private TableColumn<Map<String, Object>, String> refColumn, catColumn, desColumn, qteColumn;
    @FXML
    private ComboBox<String> categorieBox, designationBox, referenceBox;

    public visualiserStockController(DependencyManager dependencyManager){ this.dependencyManager = dependencyManager; }

    public void initialize(){
        updateCategorie();
        updateDesignation();
        updateReference();
    }

    @FXML
    public void updateFicheStockTable(List<Map<String, Object>> fichesStock) {
        if (!ficheStockTable.getItems().isEmpty()) {
            ficheStockTable.getItems().clear();
        }

        ObservableList<Map<String, Object>> observableFiches = FXCollections.observableArrayList(fichesStock);
        ficheStockTable.setItems(observableFiches);

        refColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_matierePremiere")).asString());
        catColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("categorie")).asString());
        desColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("designation")).asString());
        qteColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());

        ficheStockTable.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    String selectFicheStockId = (String) row.getItem().get("id_matierePremiere");
                    try {
                        openFicheStockWindow(selectFicheStockId, event);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return row;
        });
    }

    public void openFicheStockWindow(String ficheStockId, MouseEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("ficheStock.fxml"));
        fxmlLoader.setController(new ficheStockController(dependencyManager, ficheStockId));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
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

        for(Map<String, Object> c : references){
            referencesList.add((String)c.get("id_matierePremiere"));
        }
        referenceBox.setItems(FXCollections.observableArrayList(referencesList));
        referenceBox.setEditable(true);
    }

    public void retour(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();

    }

    public void rechercher(){
        String categorie = categorieBox.getValue();
        String desgination = designationBox.getValue();
        String reference = referenceBox.getValue();
        if (categorie != null && categorie.isEmpty()) categorie = null;
        if (desgination != null && desgination.isEmpty()) desgination = null;
        if (reference != null && reference.isEmpty()) reference = null;
        updateFicheStockTable(dependencyManager.getFicheStockRepository().filter(categorie, desgination, reference));
    }
}
