package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class accueilFiche {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TilePane ficheCards;
    private DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> ficheStockTable, bonSortieTable, facturesTable, produitFiniTable, commandeTable;
    @FXML
    private TableColumn<Map<String, Object>, String> idFactureColumn, nbrFactureColumn, fournisseurFactureColumn, dateFactureColumn, refProduitFiniColumn, refFicheStockColumn, catFicheStockColumn, desFicheStockColumn, qteFicheStockColumn, idBonColumn, dateBonColumn, idCommandeColumn;
    @FXML
    private Button createCommande, createFacture, createProduit, createBonSortie, rechercheFicheFiltre, factures, fiches, produits, bons, commandes;
    @FXML
    private Label facture, commande, produitFini, bonSortie, ficheStock;
    @FXML
    private ComboBox<String> referenceFicheFiltre, categorieFicheFiltre, designationFicheFiltre;

    public accueilFiche(DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
    }
    public void initialize() {
        updateFicheStockTable();
        updateCategorie();
        updateDesignation();
        updateReference();
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
        fxmlLoader.setController(new ficheStockController(dependencyManager, ficheStockId));
        ColorAdjust dim = new ColorAdjust();
        dim.setBrightness(-0.4);

        Node source = (Node) ignoredevent.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.getScene().getRoot().setEffect(dim);
        Scene scene = new Scene(fxmlLoader.load());
        JMetro jMetro = new JMetro(scene, Style.LIGHT);
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.showAndWait();
        dim.setBrightness(0);
        oldStage.getScene().getRoot().setEffect(dim);
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
        categorieFicheFiltre.setItems(FXCollections.observableArrayList(categoriesList));
        categorieFicheFiltre.setEditable(true);

        autoCompletion(categorieFicheFiltre, categoriesList);
    }
    public void updateDesignation(){
        List<Map<String, Object>> designations = dependencyManager.getDesignationRepository().findAll();
        List<String> designationsList = new ArrayList<>();

        for(Map<String, Object> c : designations){
            designationsList.add((String)c.get("nom"));
        }
        designationFicheFiltre.setItems(FXCollections.observableArrayList(designationsList));
        designationFicheFiltre.setEditable(true);

        autoCompletion(designationFicheFiltre, designationsList);

    }
    public void updateReference(){
        List<Map<String, Object>> references = dependencyManager.getFicheStockRepository().getAllId();
        List<String> referencesList = new ArrayList<>();

        for(Map<String, Object> c : references){
            referencesList.add((String)c.get("id_matierePremiere"));
        }
        referenceFicheFiltre.setItems(FXCollections.observableArrayList(referencesList));
        referenceFicheFiltre.setEditable(true);

        autoCompletion(referenceFicheFiltre, referencesList);
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

}
