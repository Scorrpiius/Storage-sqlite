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
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class commandeController {
    @FXML
    private AnchorPane root;
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

    public commandeController(DependencyManager dependencyManager, String commandeId, AnchorPane root){
        this.dependencyManager = dependencyManager;
        this.commandeId = commandeId;
        this.root = root;
    }

    public void initialize(){
        updateProduitsTable();
        updateData();
        produitTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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

    public void modifierCommande(ActionEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("modifierCommande.fxml"));
        fxmlLoader.setController(new modifierCommandeController(dependencyManager, commandeId, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);
    }
    public void retourAccueil(ActionEvent event) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilCommande.fxml"));
        fxmlLoader.setController(new accueilCommande(dependencyManager, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);
    }
    public void visualiserTableauBesoin(ActionEvent ignoredEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("tableauBesoin.fxml"));
        fxmlLoader.setController(new tableauBesoinController(dependencyManager, commandeId, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);
    }
}
