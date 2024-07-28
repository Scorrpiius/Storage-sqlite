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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class commandeController {

    private final String commandeId;
    @FXML
    private TextArea descriptionCommande;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> produitTable;
    @FXML
    private TextField idCommandeField, dateCommandeField;
    @FXML
    private TableColumn<Map<String, Object>, String> idProduitCol, qteProduitCol;

    public commandeController(DependencyManager dependencyManager, String commandeId){
        this.dependencyManager = dependencyManager;
        this.commandeId = commandeId;
    }
    public void initialize(){
        updateProduitsTable();
        updateData();
    }
    public void updateData(){
        Map<String, Object> commande = dependencyManager.getCommandeRepository().findById(commandeId);
        idCommandeField.setText(String.valueOf(commande.get("id")));
        dateCommandeField.setText(commande.get("date").toString());
        descriptionCommande.setText((String) commande.get("description"));

        idCommandeField.setEditable(false);
        dateCommandeField.setEditable(false);
        descriptionCommande.setEditable(false);
    }
    public void updateProduitsTable(){
        if (!produitTable.getItems().isEmpty()) {
            produitTable.getItems().clear();
        }

        List<Map<String, Object>> produits = dependencyManager.getCommandeProduitFiniRepository().findByCommandeId(commandeId);
        ObservableList<Map<String, Object>> observableProduits = FXCollections.observableArrayList(produits);
        produitTable.setItems(observableProduits);

        idProduitCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_ProduitFini")).asString());
        qteProduitCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());

    }
    public void modifier(ActionEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("modifierCommande.fxml"));
        fxmlLoader.setController(new modifierCommandeController(dependencyManager, commandeId));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.showAndWait();
        updateProduitsTable();
    }
    public void retour(ActionEvent event) throws IOException, SQLException {
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }
    public void tableauBesoin(ActionEvent ignoredEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("tableauBesoin.fxml"));
        fxmlLoader.setController(new tableauBesoinController(dependencyManager, commandeId));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.showAndWait();
    }
}
