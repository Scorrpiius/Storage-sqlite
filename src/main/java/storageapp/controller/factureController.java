package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class factureController {
    @FXML
    private AnchorPane root;
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

    public factureController(DependencyManager dependencyManager, String factureId, AnchorPane root){
        this.dependencyManager = dependencyManager;
        this.factureId = factureId;
        this.root = root;
    }
    public void initialize(){
        titleLabel.setText("Facture N° " + factureId);
        updateHistorique();
        updateAllData();
        matierePremiereTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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
        catMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("categorie")).asString());
        qteMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());
        pxUnitMatPremCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("px_unitaire")).asString());
        pxRevientDeviseMatPremColumn.setCellValueFactory(cellData -> {
            Object objet = cellData.getValue().get("px_revient_devise");
            return new SimpleObjectProperty<>(String.format("%.2f", objet));
        });
        pxRevientLocalMatPremColumn.setCellValueFactory(cellData -> {
            Object objet = cellData.getValue().get("px_revient_local");
            return new SimpleObjectProperty<>(String.format("%.2f", objet));
        });
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

    public void retourAccueil(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilFacture.fxml"));
        fxmlLoader.setController(new accueilFacture(dependencyManager, root));

        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);
    }
    public void modifierFacture(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("modifierFacture.fxml"));
        fxmlLoader.setController(new modifierFactureController(dependencyManager, factureId, root));

        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);

    }
}
