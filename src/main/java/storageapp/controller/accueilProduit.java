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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class accueilProduit {
    @FXML
    private AnchorPane root;

    private DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> produitFiniTable;
    @FXML
    private TableColumn<Map<String, Object>, String> refProduitFiniColumn;

    public accueilProduit(DependencyManager dependencyManager, AnchorPane root) {
        this.dependencyManager = dependencyManager;
        this.root = root;
    }
    public void initialize() {
        updateProduitFiniTable();
        produitFiniTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }
    @FXML
    protected void ajouterProduitFini(ActionEvent ignorede) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("nouveauProduitFini.fxml"));
        fxmlLoader.setController(new nouveauProduitFiniController(dependencyManager, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);

    }
    public void openProduitFiniWindow(String produitId, MouseEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("produitFini.fxml"));
        fxmlLoader.setController(new produitFiniController(dependencyManager, produitId, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);

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
    }
}
