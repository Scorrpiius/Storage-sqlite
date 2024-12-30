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
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class accueilFacture {
    @FXML
    private AnchorPane root;
    private DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> facturesTable;
    @FXML
    private TableColumn<Map<String, Object>, String> idFactureColumn, nbrFactureColumn, fournisseurFactureColumn, dateFactureColumn;

    public accueilFacture(DependencyManager dependencyManager, AnchorPane root) {
        this.dependencyManager = dependencyManager;
        this.root = root;
    }
    public void initialize() {
        updateFactureTable();
        facturesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
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
    public void openFactureWindow(String factureId, MouseEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("facture.fxml"));
        fxmlLoader.setController(new factureController(dependencyManager, factureId, root));

        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);

    }

    @FXML
    protected void ajouterFacture(ActionEvent ignorede) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("nouvelleFacture.fxml"));
        fxmlLoader.setController(new nouvelleFactureController(dependencyManager, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);
    }


}
