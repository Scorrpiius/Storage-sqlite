package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class accueilCommande {
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
    public accueilCommande(DependencyManager dependencyManager) {this.dependencyManager = dependencyManager;}
    public void initialize() {
        updateCommandesTable();
    }

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

    public void openCommandeWindow(String commandeID, MouseEvent ignoredevent) throws IOException {
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
    }
}
