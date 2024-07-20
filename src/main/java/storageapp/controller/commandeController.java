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
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class commandeController {
    private final String commandeId;
    private final DependencyManager dependencyManager;
    @FXML
    private TextField id, date;
    @FXML
    private TableView<Map<String, Object>> produitTable, listeBesoin;
    @FXML
    private TableColumn<Map<String, Object>, String> idColumn, qteColumn, refMatiere, qteMatiere;
    @FXML
    private TextArea description;

    public commandeController(DependencyManager dependencyManager, String commandeId){
        this.dependencyManager = dependencyManager;
        this.commandeId = commandeId;
    }

    public void initialize(){
        updateProduitsTable();
        updateBesoinsTable();
        updateData();
    }

    @FXML
    public void updateData(){
        Map<String, Object> commande = dependencyManager.getCommandeRepository().findById(commandeId);
        id.setText(String.valueOf(commande.get("id")));
        date.setText(commande.get("date").toString());
        description.setText((String) commande.get("description"));

        id.setEditable(false);
        date.setEditable(false);
        description.setEditable(false);
    }

    @FXML
    public void updateProduitsTable(){
        if (!produitTable.getItems().isEmpty()) {
            produitTable.getItems().clear();
        }

        List<Map<String, Object>> produits = dependencyManager.getCommandeProduitFiniRepository().findByCommandeId(commandeId);
        ObservableList<Map<String, Object>> observableProduits = FXCollections.observableArrayList(produits);
        produitTable.setItems(observableProduits);

        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_ProduitFini")).asString());
        qteColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());

    }

    @FXML
    public void updateBesoinsTable(){
        List<Map<String, Object>> besoins = dependencyManager.getCommandeMatierePremiereRepository().findByCommandeId(commandeId);

        ObservableList<Map<String, Object>> observableBesoins = FXCollections.observableArrayList(besoins);
        listeBesoin.setItems(observableBesoins);

        refMatiere.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_MatierePremiere")).asString());
        qteMatiere.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());


    }

    public void modifier(ActionEvent event){}

    public void retour(ActionEvent event) throws IOException, SQLException {
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("MainFinal.fxml"));
        fxmlLoader.setController(new mainController(dependencyManager));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}
