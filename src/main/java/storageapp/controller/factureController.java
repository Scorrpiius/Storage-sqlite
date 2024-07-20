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
import java.util.List;
import java.util.Map;

public class factureController {
    @FXML
    private Label titlePage;
    private final String factureId;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> matPremTableau;
    @FXML
    private TextField referenceFacture, dateFacture, valeurDevise, nomDevise, txTheo, txReel, fournisseurFacture;
    @FXML
    private TableColumn<Map<String, Object>, String> refMatPremCol, desMatPremCol, qteMatPremCol, pxUnitMatPremCol, pxRevientDeviseMatPremColumn, pxRevientLocalMatPremColumn, catMatPremCol;

    public factureController(DependencyManager dependencyManager, String factureId){
        this.dependencyManager = dependencyManager;
        this.factureId = factureId;
    }
    public void initialize(){
        titlePage.setText("Facture N° " + factureId);
        updateHistorique();
        updateAllData();
    }
    public void updateHistorique(){
        List<Map<String,Object>> historiqueData = dependencyManager.getEntreeRepository().findByFactureId(factureId);

        if (!matPremTableau.getItems().isEmpty()) {
            matPremTableau.getItems().clear();
        }

        ObservableList<Map<String, Object>> observableHistorique = FXCollections.observableArrayList(historiqueData);
        matPremTableau.setItems(observableHistorique);

        refMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_reference")).asString());
        desMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("designation")).asString());
        qteMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());
        pxUnitMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("px_unitaire")).asString());
        pxRevientDeviseMatPremColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("px_revient_devise")).asString());
        pxRevientLocalMatPremColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("px_revient_local")).asString());
        catMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("categorie")).asString());
    }
    public void updateAllData(){
        Map<String, Object> facture = dependencyManager.getFactureRepository().findById(factureId);
        String fournisseur = (String) facture.get("fournisseur");
        String date = (String) facture.get("date");
        String deviseValue = String.valueOf(facture.get("valeur_devise"));
        String deviseName = (String) facture.get("nom_devise");
        String txReel = String.valueOf(facture.get("tx_reel"));
        String txTheo = String.valueOf(facture.get("tx_theo"));
        pxRevientDeviseMatPremColumn.setText("Prix de revient \n("+deviseName+")");

        referenceFacture.setText(factureId);
        this.fournisseurFacture.setText(fournisseur);
        valeurDevise.setText(deviseValue);
        nomDevise.setText(deviseName);
        this.txTheo.setText(txTheo);
        this.txReel.setText(txReel);
        this.dateFacture.setText(date);

        referenceFacture.setEditable(false);
        this.fournisseurFacture.setEditable(false);
        valeurDevise.setEditable(false);
        nomDevise.setEditable(false);
        this.txTheo.setEditable(false);
        this.txReel.setEditable(false);
        this.dateFacture.setEditable(false);
    }
    public void retour(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }
    public void modifier(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("modifierFacture.fxml"));
        fxmlLoader.setController(new modifierFactureController(dependencyManager, factureId));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.showAndWait();
        updateAllData();
        updateHistorique();
    }
}
