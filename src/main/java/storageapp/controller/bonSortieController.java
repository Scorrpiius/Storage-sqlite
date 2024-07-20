package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class bonSortieController {
    @FXML
    private Label titlePage;
    private final String bonSortieId;
    @FXML
    private TextField idBonField, dateBon;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> sortiesTableau;
    @FXML
    private TableColumn<Map<String, Object>, String> idColumn, refColumn, qteColumn, descColumn;

    public bonSortieController(DependencyManager dependencyManager, String bonSortieId){
        this.dependencyManager = dependencyManager;
        this.bonSortieId = bonSortieId;
    }

    public void initialize(){
        titlePage.setText("Bon de sortie N° " + bonSortieId);
        updateSortieTable();
        updateData();
    }

    @FXML
    public void updateData(){
        Map<String, Object> bonSortie = dependencyManager.getBonSortieRepository().findById(bonSortieId);
        idBonField.setEditable(false);
        dateBon.setEditable(false);

        idBonField.setText(String.valueOf(bonSortie.get("id")));
        dateBon.setText((String) bonSortie.get("date"));
    }

    @FXML
    public void updateSortieTable(){
        if (!sortiesTableau.getItems().isEmpty()) {
            sortiesTableau.getItems().clear();
        }

        List<Map<String, Object>> sorties = dependencyManager.getSortieRepository().getAllSortiesByBon(String.valueOf(bonSortieId));
        ObservableList<Map<String, Object>> observableSorties = FXCollections.observableArrayList(sorties);
        sortiesTableau.setItems(observableSorties);

        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id")).asString());
        refColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_matierePremiere")).asString());
        qteColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());
        descColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("description")).asString());
    }

    public void retour(ActionEvent event) throws IOException, SQLException {
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }

    public void modifier(ActionEvent event) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("modifierBon.fxml"));
        fxmlLoader.setController(new modifierBonController(dependencyManager, bonSortieId));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.showAndWait();
        updateSortieTable();
    }
}
