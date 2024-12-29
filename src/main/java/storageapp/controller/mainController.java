package storageapp.controller;


import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
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

public class mainController {
    @FXML
    private AnchorPane anchorPane, centralPane;
    @FXML
    private TilePane ficheCards;
    private final DependencyManager dependencyManager;
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
    public mainController(DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
    }
    public void initialize() throws IOException, InterruptedException {

        /*FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilFacture.fxml"));
        fxmlLoader.setController(new accueilFacture(this.dependencyManager));
        AnchorPane newLoadedPane = fxmlLoader.load();

        centralPane.getChildren().setAll(newLoadedPane);*/

        //setButtons();

        //updateFactureTable();
        //updateFicheStockTable();
        //updateBonSortieTable();
        //updateProduitFiniTable();
        //updateCommandesTable();
        /*ficheStockTable.setVisible(false);
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
        updateDesignation();*/
    }

    /*public void updateCommandesTable(){
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
    }*/
    /*public void updateFactureTable() {
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
    }*/

    /*public void openCommandeWindow(String commandeID, MouseEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("commande.fxml"));
        fxmlLoader.setController(new commandeController(dependencyManager, commandeID));
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
    public void openBonWindow(String bonSortieID, MouseEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("bonSortie.fxml"));
        fxmlLoader.setController(new bonSortieController(dependencyManager, bonSortieID));
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
        updateFicheStockTable();
        updateBonSortieTable();
    }
    public void openProduitFiniWindow(String produitId, MouseEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("produitFini.fxml"));
        fxmlLoader.setController(new produitFiniController(dependencyManager,produitId ));
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
        updateProduitFiniTable();

    }*/
    public void openFactureWindow(String factureId, MouseEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("facture.fxml"));
        fxmlLoader.setController(new factureController(dependencyManager, factureId));
        ColorAdjust dim = new ColorAdjust();
        dim.setBrightness(-0.4);

        Node source = (Node) ignoredevent.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.getScene().getRoot().setEffect(dim);
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        JMetro jMetro = new JMetro(scene, Style.LIGHT);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.showAndWait();
        dim.setBrightness(0);
        oldStage.getScene().getRoot().setEffect(dim);
        //updateFactureTable();
        //updateFicheStockTable();
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
    protected void ajouterNouvelleFacture(ActionEvent ignorede) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("nouvelleFacture.fxml"));
        fxmlLoader.setController(new nouvelleFactureController(dependencyManager));
        ColorAdjust dim = new ColorAdjust();
        dim.setBrightness(-0.4);

        Node source = (Node) ignorede.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.getScene().getRoot().setEffect(dim);

        Scene scene = new Scene(fxmlLoader.load());
        JMetro jMetro = new JMetro(scene, Style.LIGHT);
        Stage stage = new Stage();
        //stage.setTitle("Stockapp");
        //stage.setMaximized(true);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.showAndWait();
        dim.setBrightness(0);
        oldStage.getScene().getRoot().setEffect(dim);
        //updateFicheStockTable();
        //updateFactureTable();
    }
    /*@FXML
    protected void creerProduitFini(ActionEvent ignorede) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("nouveauProduitFini.fxml"));
        fxmlLoader.setController(new nouveauProduitFiniController(dependencyManager));
        ColorAdjust dim = new ColorAdjust();
        dim.setBrightness(-0.4);

        Node source = (Node) ignorede.getSource();
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
        updateProduitFiniTable();
        //updateProduitCard();

    }
    @FXML
    protected void creerBon(ActionEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("nouveauBonSortie.fxml"));
        fxmlLoader.setController(new nouveauBonController(dependencyManager));
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
        updateBonSortieTable();
        updateFicheStockTable();
    }
    @FXML
    protected void creerCommande(ActionEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("nouvelleCommande.fxml"));
        fxmlLoader.setController(new nouvelleCommandeController(dependencyManager));
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
        updateCommandesTable();
    }*/


    @FXML
    protected void showFacture(ActionEvent ignoredevent) throws IOException {

        /*updateFactureTable();
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
        rechercheFicheFiltre.setVisible(false);*/
        /*for (var node : ficheCards.getChildren()) {
            node.setVisible(false);
        }*/
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilFacture.fxml"));
        fxmlLoader.setController(new accueilFacture(dependencyManager));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setMinHeight(centralPane.getHeight());
        newLoadedPane.setMinWidth(centralPane.getWidth());
        centralPane.getChildren().setAll(newLoadedPane);

    }
    @FXML
    protected void showFiches(ActionEvent ignoredevent) throws IOException {
        /*updateFicheStockTable();
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
        /*for (var node : ficheCards.getChildren()) {
            node.setVisible(false);
        }*/
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilFiche.fxml"));
        fxmlLoader.setController(new accueilFiche(dependencyManager));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setMinHeight(centralPane.getHeight());
        newLoadedPane.setMinWidth(centralPane.getWidth());
        centralPane.getChildren().setAll(newLoadedPane);
    }
    @FXML
    protected void showCommandes(ActionEvent ignoredevent) throws IOException {
        /*facturesTable.setVisible(false);
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
        /*for (var node : ficheCards.getChildren()) {
            node.setVisible(false);
        }*/
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilCommande.fxml"));
        fxmlLoader.setController(new accueilCommande(dependencyManager));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setMinHeight(centralPane.getHeight());
        newLoadedPane.setMinWidth(centralPane.getWidth());
        centralPane.getChildren().setAll(newLoadedPane);

    }
    @FXML
    protected void showProduits(ActionEvent ignoredevent) throws IOException {
        /*for (var node : ficheCards.getChildren()) {
            node.setVisible(true);
        }
        updateProduitFiniTable();
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
        //updateProduitCard();*/
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilProduit.fxml"));
        fxmlLoader.setController(new accueilProduit(dependencyManager));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setMinHeight(centralPane.getHeight());
        newLoadedPane.setMinWidth(centralPane.getWidth());
        centralPane.getChildren().setAll(newLoadedPane);

    }
    @FXML
    protected void showBons(ActionEvent ignoredevent) throws IOException {
        //updateBonSortieTable();
        /*facturesTable.setVisible(false);
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
        rechercheFicheFiltre.setVisible(false);*/
        /*for (var node : ficheCards.getChildren()) {
            node.setVisible(false);
        }*/
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilBon.fxml"));
        fxmlLoader.setController(new accueilBon(dependencyManager));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setMinHeight(centralPane.getHeight());
        newLoadedPane.setMinWidth(centralPane.getWidth());
        centralPane.getChildren().setAll(newLoadedPane);
    }

    /*public void updateCategorie(){
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
    @FXML
    public void setButtons(){
        /*String url = "E:\\Stockapp\\Storage-sqlite\\src\\main\\resources\\css\\home.png";
        Image img = new Image(url);
        ImageView imageView = new ImageView(img);
        factures.setGraphic(imageView);
    }*/


    public void loadFxml (ActionEvent ignoredevent) throws IOException {
        Pane newLoadedPane = FXMLLoader.load(StorageApp.class.getResource("accueilFacture.fxml"));
        centralPane.getChildren().setAll(newLoadedPane);
    }
}

