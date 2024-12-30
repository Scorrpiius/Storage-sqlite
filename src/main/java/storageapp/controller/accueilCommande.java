package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class accueilCommande {
    @FXML
    private AnchorPane root;

    private DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> commandeTable;
    @FXML
    private TableColumn<Map<String, Object>, String> idCommandeColumn;

    public accueilCommande(DependencyManager dependencyManager, AnchorPane root) {
        this.dependencyManager = dependencyManager;
        this.root = root;
    }
    public void initialize() {
        updateCommandesTable();
        commandeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
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
        fxmlLoader.setController(new commandeController(dependencyManager, commandeID, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);
    }
    @FXML
    protected void ajouterCommande(ActionEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("nouvelleCommande.fxml"));
        fxmlLoader.setController(new nouvelleCommandeController(dependencyManager, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);

    }
}
