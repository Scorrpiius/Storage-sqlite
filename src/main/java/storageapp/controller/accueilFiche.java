package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.textfield.TextFields;
import storageapp.StorageApp;
import storageapp.model.FicheStockRepository;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class accueilFiche {
    @FXML
    private AnchorPane root;
    private DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> ficheStockTable;
    @FXML
    private TableColumn<Map<String, Object>, String> refFicheStockColumn, catFicheStockColumn, desFicheStockColumn, qteFicheStockColumn;
    @FXML
    private ComboBox<String> referenceFicheFiltre, categorieFicheFiltre, designationFicheFiltre;

    public accueilFiche(DependencyManager dependencyManager, AnchorPane root) {
        this.dependencyManager = dependencyManager;
        this.root = root;
    }

    public void initialize() {
        updateFicheStockTable();
        updateCategorie();
        updateDesignation();
        updateReference();
        ficheStockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }
    
    public void updateFicheStockTable() {
        if (!ficheStockTable.getItems().isEmpty()) {
            ficheStockTable.getItems().clear();
        }

        List<Map<String, Object>> fichesStock = dependencyManager.getFicheStockRepository().findAll();
        ObservableList<Map<String, Object>> observableFiches = FXCollections.observableArrayList(fichesStock);
        ficheStockTable.setItems(observableFiches);

        refFicheStockColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_matierePremiere")).asString());
        catFicheStockColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("categorie")).asString());
        desFicheStockColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("designation")).asString());
        qteFicheStockColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());

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
    public void updateFicheStock(List<Map<String, Object>> fichesStock){
        if (!ficheStockTable.getItems().isEmpty()) {
            ficheStockTable.getItems().clear();
        }

        ObservableList<Map<String, Object>> observableFiches = FXCollections.observableArrayList(fichesStock);
        ficheStockTable.setItems(observableFiches);

        refFicheStockColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_matierePremiere")).asString());
        catFicheStockColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("categorie")).asString());
        desFicheStockColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("designation")).asString());
        qteFicheStockColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());

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
    public void openFicheStockWindow(String ficheStockId, MouseEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("ficheStock.fxml"));
        fxmlLoader.setController(new ficheStockController(dependencyManager, ficheStockId, root));

        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);
    }

    @FXML
    public void rechercheFiche(ActionEvent ignoredevent){
        String categorie = categorieFicheFiltre.getValue();
        String desgination = designationFicheFiltre.getValue();
        String reference = referenceFicheFiltre.getValue();
        if (categorie != null && categorie.isEmpty()) categorie = null;
        if (desgination != null && desgination.isEmpty()) desgination = null;
        if (reference != null && reference.isEmpty()) reference = null;
        updateFicheStock(dependencyManager.getFicheStockRepository().filter(categorie, desgination, reference));
    }

    public void updateCategorie(){
        List<Map<String, Object>> categories = dependencyManager.getCategorieRepository().findAll();
        List<String> categoriesList = new ArrayList<>();

        for(Map<String, Object> c : categories){
            categoriesList.add((String)c.get("nom"));
        }
        ObservableList<String> items = FXCollections.observableArrayList(categoriesList);
        categorieFicheFiltre.setItems(FXCollections.observableArrayList(categoriesList));
        categorieFicheFiltre.setEditable(true);
        TextFields.bindAutoCompletion(categorieFicheFiltre.getEditor(), categorieFicheFiltre.getItems());


    }
    public void updateDesignation(){
        List<Map<String, Object>> designations = dependencyManager.getDesignationRepository().findAll();
        List<String> designationsList = new ArrayList<>();

        for(Map<String, Object> c : designations){
            designationsList.add((String)c.get("nom"));
        }
        ObservableList<String> items = FXCollections.observableArrayList(designationsList);
        designationFicheFiltre.setItems(FXCollections.observableArrayList(designationsList));
        designationFicheFiltre.setEditable(true);
        TextFields.bindAutoCompletion(designationFicheFiltre.getEditor(), designationFicheFiltre.getItems());
    }

    public void updateReference(){
        List<Map<String, Object>> references = dependencyManager.getFicheStockRepository().getAllId();
        List<String> referencesList = new ArrayList<>();

        for(Map<String, Object> c : references){
            referencesList.add((String)c.get("id_matierePremiere"));
        }
        ObservableList<String> items = FXCollections.observableArrayList(referencesList);
        referenceFicheFiltre.setItems(FXCollections.observableArrayList(referencesList));
        referenceFicheFiltre.setEditable(true);

        TextFields.bindAutoCompletion(referenceFicheFiltre.getEditor(), referenceFicheFiltre.getItems());
    }

}
