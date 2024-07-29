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
    private Label titleLabel;
    private final String factureId;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> matierePremiereTable;
    @FXML
    private TextField referenceFactureField, dateFactureField, valeurDeviseField, nomDeviseField, tauxTheoField, tauxReelField, fournisseurFactureField;
    @FXML
    private TableColumn<Map<String, Object>, String> refMatPremCol, desMatPremCol, qteMatPremCol, pxUnitMatPremCol, pxRevientDeviseMatPremColumn, pxRevientLocalMatPremColumn, catMatPremCol;

    public factureController(DependencyManager dependencyManager, String factureId){
        this.dependencyManager = dependencyManager;
        this.factureId = factureId;
    }
    public void initialize(){
        titleLabel.setText("Facture N° " + factureId);
        updateHistorique();
        updateAllData();
    }
    public void updateHistorique(){
        List<Map<String,Object>> historiqueData = dependencyManager.getEntreeRepository().findByFactureId(factureId);

        if (!matierePremiereTable.getItems().isEmpty()) {
            matierePremiereTable.getItems().clear();
        }

        ObservableList<Map<String, Object>> observableHistorique = FXCollections.observableArrayList(historiqueData);
        matierePremiereTable.setItems(observableHistorique);

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

        referenceFactureField.setText(factureId);
        this.fournisseurFactureField.setText(fournisseur);
        valeurDeviseField.setText(deviseValue);
        nomDeviseField.setText(deviseName);
        this.tauxTheoField.setText(txTheo);
        this.tauxReelField.setText(txReel);
        this.dateFactureField.setText(date);

        referenceFactureField.setEditable(false);
        this.fournisseurFactureField.setEditable(false);
        valeurDeviseField.setEditable(false);
        nomDeviseField.setEditable(false);
        this.tauxTheoField.setEditable(false);
        this.tauxReelField.setEditable(false);
        this.dateFactureField.setEditable(false);
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
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }
}
