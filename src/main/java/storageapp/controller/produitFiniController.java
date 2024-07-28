package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public class produitFiniController {
    @FXML
    private ImageView img;
    @FXML
    private Label titlePage;
    private final String produitID;
    @FXML
    private TextField referenceProduit;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> matierePremiereTable;
    @FXML
    private TableColumn<Map<String, Object>, String> refColumn, qteColumn;

    public produitFiniController(DependencyManager dependencyManager, String produitID){
        this.dependencyManager = dependencyManager;
        this.produitID = produitID;
    }
    public void initialize(){
        titlePage.setText("Produit " + produitID);
        titlePage.setAlignment(Pos.CENTER);
        updateMatiereTable();
        updateData();
        loadImage();
    }
    @FXML
    public void loadImage(){
        dependencyManager.getProduitFiniRepository().getPicture(produitID, null, img);
    }

    @FXML
    public void updateData(){
        referenceProduit.setText(produitID);
        referenceProduit.setEditable(false);
    }
    @FXML
    public void updateMatiereTable(){
        if (!matierePremiereTable.getItems().isEmpty()) {
            matierePremiereTable.getItems().clear();
        }

        List<Map<String, Object>> matieresPremiere = dependencyManager.getProduitFiniMatierePremiereRepository().findByProduitId(produitID);
        if (matieresPremiere != null){
            ObservableList<Map<String, Object>> observableMatierePremieres = FXCollections.observableArrayList(matieresPremiere);
            matierePremiereTable.setItems(observableMatierePremieres);

            refColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("matiere_id")).asString());
            qteColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());
        }

    }
    public void retour(ActionEvent e) throws SQLException, IOException {
        Node source = (Node) e.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }
    public void modifier(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("modifierProduit.fxml"));
        fxmlLoader.setController(new modifierProduitFiniController(dependencyManager, produitID));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.showAndWait();
        Node source = (Node) e.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
        //updateMatiereTable();
    }
}
