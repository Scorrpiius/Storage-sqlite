package storageapp.controller;


import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class mainController {
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> ficheStockTable, bonSortieTable, facturesTable, produitFiniTable, commandeTable;
    @FXML
    private TableColumn<Map<String, Object>, String> idFactureColumn, nbrFactureColumn, fournisseurFactureColumn, dateFactureColumn, refProduitFiniColumn, refFicheStockColumn, catFicheStockColumn, desFicheStockColumn, qteFicheStockColumn, idBonColumn, dateBonColumn, idCommandeColumn;
    @FXML
    private Button createCommande, createFacture, createProduit, createBonSortie, rechercheFicheFiltre;
    @FXML
    private Label facture, commande, produitFini, bonSortie, ficheStock;
    @FXML
    private ComboBox<String> referenceFicheFiltre, categorieFicheFiltre, designationFicheFiltre;
    public mainController(DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
    }
    public void initialize(){

        facturesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        bonSortieTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ficheStockTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        updateFactureTable();
        updateFicheStockTable();
        updateBonSortieTable();
        updateProduitFiniTable();
        updateCommandesTable();
        ficheStockTable.setVisible(false);
        bonSortieTable.setVisible(false);
        produitFiniTable.setVisible(false);
        commandeTable.setVisible(false);

        createCommande.setVisible(false);
        createBonSortie.setVisible(false);
        createProduit.setVisible(false);

        referenceFicheFiltre.setVisible(false);
        categorieFicheFiltre.setVisible(false);
        designationFicheFiltre.setVisible(false);
        rechercheFicheFiltre.setVisible(false);

        updateReference();
        updateCategorie();
        updateDesignation();
    }

    @FXML
    public void updateCommandesTable(){
        if (!commandeTable.getItems().isEmpty()) {
            commandeTable.getItems().clear();
        }

        List<Map<String, Object>> commandes = dependencyManager.getCommandeRepository().findAll();
        ObservableList<Map<String, Object>> observableCommandes = FXCollections.observableArrayList(commandes);
        commandeTable.setItems(observableCommandes);

        idCommandeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id")).asString());

        commandeTable.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    String selectCommandeId = (String) row.getItem().get("id");
                    try {
                        openCommandeWindow(selectCommandeId, event);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return row;
        });

    }

    public void openCommandeWindow(String commandeID, MouseEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("commande.fxml"));
        fxmlLoader.setController(new commandeController(dependencyManager, commandeID));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void updateBonSortieTable(){
        if (!bonSortieTable.getItems().isEmpty()) {
            bonSortieTable.getItems().clear();
        }

        List<Map<String, Object>> bonSorties = dependencyManager.getBonSortieRepository().findAll();
        ObservableList<Map<String, Object>> observableBonsSorties = FXCollections.observableArrayList(bonSorties);
        bonSortieTable.setItems(observableBonsSorties);

        idBonColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id")).asString());
        dateBonColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("date")).asString());

        bonSortieTable.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    String selectBonSortieId = (String) row.getItem().get("id");
                    try {
                        openBonWindow(selectBonSortieId, event);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return row;
        });
    }

    public void openBonWindow(String bonSortieID, MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("bonSortie.fxml"));
        fxmlLoader.setController(new bonSortieController(dependencyManager, bonSortieID));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.showAndWait();
        updateFicheStockTable();
        updateBonSortieTable();
    }

    @FXML
    public void updateProduitFiniTable(){
        if (!produitFiniTable.getItems().isEmpty()) {
            produitFiniTable.getItems().clear();
        }

        List<Map<String, Object>> produitFinis = dependencyManager.getProduitFiniRepository().findAll();
        ObservableList<Map<String, Object>> observableProduitFini = FXCollections.observableArrayList(produitFinis);
        produitFiniTable.setItems(observableProduitFini);

        refProduitFiniColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("reference")).asString());

        produitFiniTable.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    String produitId = (String) row.getItem().get("reference");
                    try {
                        openProduitFiniWindow(produitId, event);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return row;
        });
    }

    public void openProduitFiniWindow(String produitId, MouseEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("produitFini.fxml"));
        fxmlLoader.setController(new produitFiniController(dependencyManager,produitId ));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.showAndWait();

    }
    @FXML
    public void updateFactureTable() {
        if (!facturesTable.getItems().isEmpty()) {
            facturesTable.getItems().clear();
        }

        List<Map<String, Object>> factures = dependencyManager.getFactureRepository().findAll();
        ObservableList<Map<String, Object>> observableFactures = FXCollections.observableArrayList(factures);
        facturesTable.setItems(observableFactures);

        idFactureColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id")).asString());
        fournisseurFactureColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("fournisseur")).asString());
        dateFactureColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("date")).asString());
        nbrFactureColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("nbr_matPrem")).asString());

        facturesTable.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    String selectFactureId = (String) row.getItem().get("id");
                    try {
                        openFactureWindow(selectFactureId, event);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return row;
        });
    }

    public void openFactureWindow(String factureId, MouseEvent event) throws IOException {
        /*Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();*/

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("facture.fxml"));
        fxmlLoader.setController(new factureController(dependencyManager, factureId));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.showAndWait();
        updateFactureTable();
        updateFicheStockTable();
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

    @FXML
    protected void ajouterNouvelleFacture(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("nouvelleFacture.fxml"));
        fxmlLoader.setController(new nouvelleFactureController(dependencyManager));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.showAndWait();
        updateFicheStockTable();
        updateFactureTable();
    }

    @FXML
    protected void creerProduitFini(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("nouveauProduitFini.fxml"));
        fxmlLoader.setController(new nouveauProduitFiniController(dependencyManager));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.showAndWait();
        updateProduitFiniTable();
    }

    @FXML
    protected void creerBon(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("nouveauBonSortie.fxml"));
        fxmlLoader.setController(new nouveauBonController(dependencyManager));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.showAndWait();
        updateBonSortieTable();
        updateFicheStockTable();
    }

    @FXML
    protected void creerCommande(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("nouvelleCommande.fxml"));
        fxmlLoader.setController(new nouvelleCommandeController(dependencyManager));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    protected void showFacture(ActionEvent event){
        facturesTable.setVisible(true);
        ficheStockTable.setVisible(false);
        bonSortieTable.setVisible(false);
        produitFiniTable.setVisible(false);
        commandeTable.setVisible(false);

        createFacture.setVisible(true);
        createCommande.setVisible(false);
        createBonSortie.setVisible(false);
        createProduit.setVisible(false);

        facture.setVisible(true);
        commande.setVisible(false);
        produitFini.setVisible(false);
        bonSortie.setVisible(false);
        ficheStock.setVisible(false);

        referenceFicheFiltre.setVisible(false);
        categorieFicheFiltre.setVisible(false);
        designationFicheFiltre.setVisible(false);
        rechercheFicheFiltre.setVisible(false);

    }

    @FXML
    protected void showFiches(ActionEvent event){
        facturesTable.setVisible(false);
        ficheStockTable.setVisible(true);
        bonSortieTable.setVisible(false);
        produitFiniTable.setVisible(false);
        commandeTable.setVisible(false);

        createFacture.setVisible(false);
        createCommande.setVisible(false);
        createBonSortie.setVisible(false);
        createProduit.setVisible(false);

        facture.setVisible(false);
        commande.setVisible(false);
        produitFini.setVisible(false);
        bonSortie.setVisible(false);
        ficheStock.setVisible(true);

        referenceFicheFiltre.setVisible(true);
        categorieFicheFiltre.setVisible(true);
        designationFicheFiltre.setVisible(true);
        rechercheFicheFiltre.setVisible(true);
    }
    @FXML
    protected void showCommandes(ActionEvent event){
        facturesTable.setVisible(false);
        ficheStockTable.setVisible(false);
        bonSortieTable.setVisible(false);
        produitFiniTable.setVisible(false);
        commandeTable.setVisible(true);

        createFacture.setVisible(false);
        createCommande.setVisible(true);
        createBonSortie.setVisible(false);
        createProduit.setVisible(false);

        facture.setVisible(false);
        commande.setVisible(true);
        produitFini.setVisible(false);
        bonSortie.setVisible(false);
        ficheStock.setVisible(false);

        referenceFicheFiltre.setVisible(false);
        categorieFicheFiltre.setVisible(false);
        designationFicheFiltre.setVisible(false);
        rechercheFicheFiltre.setVisible(false);
    }
    @FXML
    protected void showProduits(ActionEvent event){
        facturesTable.setVisible(false);
        ficheStockTable.setVisible(false);
        bonSortieTable.setVisible(false);
        produitFiniTable.setVisible(true);
        commandeTable.setVisible(false);

        createFacture.setVisible(false);
        createCommande.setVisible(false);
        createBonSortie.setVisible(false);
        createProduit.setVisible(true);

        facture.setVisible(false);
        commande.setVisible(false);
        produitFini.setVisible(true);
        bonSortie.setVisible(false);
        ficheStock.setVisible(false);

        referenceFicheFiltre.setVisible(false);
        categorieFicheFiltre.setVisible(false);
        designationFicheFiltre.setVisible(false);
        rechercheFicheFiltre.setVisible(false);
    }
    @FXML
    protected void showBons(ActionEvent event){
        facturesTable.setVisible(false);
        ficheStockTable.setVisible(false);
        bonSortieTable.setVisible(true);
        produitFiniTable.setVisible(false);
        commandeTable.setVisible(false);

        createFacture.setVisible(false);
        createCommande.setVisible(false);
        createBonSortie.setVisible(true);
        createProduit.setVisible(false);

        facture.setVisible(false);
        commande.setVisible(false);
        produitFini.setVisible(false);
        bonSortie.setVisible(true);
        ficheStock.setVisible(false);

        referenceFicheFiltre.setVisible(false);
        categorieFicheFiltre.setVisible(false);
        designationFicheFiltre.setVisible(false);
        rechercheFicheFiltre.setVisible(false);
    }

    @FXML
    public void updateCategorie(){
        List<Map<String, Object>> categories = dependencyManager.getCategorieRepository().findAll();
        List<String> categoriesList = new ArrayList<>();

        for(Map<String, Object> c : categories){
            categoriesList.add((String)c.get("nom"));
        }
        categorieFicheFiltre.setItems(FXCollections.observableArrayList(categoriesList));
        categorieFicheFiltre.setEditable(true);
    }

    @FXML
    public void updateDesignation(){
        List<Map<String, Object>> designations = dependencyManager.getDesignationRepository().findAll();
        List<String> designationsList = new ArrayList<>();

        for(Map<String, Object> c : designations){
            designationsList.add((String)c.get("nom"));
        }
        designationFicheFiltre.setItems(FXCollections.observableArrayList(designationsList));
        designationFicheFiltre.setEditable(true);
    }

    @FXML
    public void updateReference(){
        List<Map<String, Object>> references = dependencyManager.getFicheStockRepository().getAllId();
        List<String> referencesList = new ArrayList<>();

        for(Map<String, Object> c : references){
            referencesList.add((String)c.get("id_matierePremiere"));
        }
        referenceFicheFiltre.setItems(FXCollections.observableArrayList(referencesList));
        referenceFicheFiltre.setEditable(true);
    }

    @FXML
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

    @FXML
    public void rechercheFiche(ActionEvent event){
        String categorie = categorieFicheFiltre.getValue();
        String desgination = designationFicheFiltre.getValue();
        String reference = referenceFicheFiltre.getValue();
        if (categorie != null && categorie.isEmpty()) categorie = null;
        if (desgination != null && desgination.isEmpty()) desgination = null;
        if (reference != null && reference.isEmpty()) reference = null;
        updateFicheStock(dependencyManager.getFicheStockRepository().filter(categorie, desgination, reference));
    }

    public void openFicheStockWindow(String ficheStockId, MouseEvent event) throws IOException {
        /*Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();*/

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("ficheStock.fxml"));
        fxmlLoader.setController(new ficheStockController(dependencyManager, ficheStockId));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.show();
    }
}
