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

public class accueilBon {
    @FXML
    private AnchorPane root;
    private DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> bonSortieTable;
    @FXML
    private TableColumn<Map<String, Object>, String> idBonColumn, dateBonColumn;

    public accueilBon(DependencyManager dependencyManager, AnchorPane root) {
        this.dependencyManager = dependencyManager;
        this.root = root;
    }
    public void initialize() {
        updateBonSortieTable();
        bonSortieTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }
    @FXML
    protected void ajouterBonSortie(ActionEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("nouveauBonSortie.fxml"));
        fxmlLoader.setController(new nouveauBonController(dependencyManager, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);
    }
    public void openBonWindow(String bonSortieID, MouseEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("bonSortie.fxml"));
        fxmlLoader.setController(new bonSortieController(dependencyManager, bonSortieID, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);
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
}
