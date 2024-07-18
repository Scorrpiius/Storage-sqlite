package stockapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.InputEvent;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import stockapp.StokageApplication;
import stockapp.model.Facture;
import stockapp.service.DependencyManager;

import java.io.IOException;
import java.util.List;

public class mainController {
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Facture> facture_table;

    @FXML
    private TableColumn<Facture, String> facture_id;
    @FXML
    private TableColumn<Facture, String> facture_fournisseur;
    @FXML
    private TableColumn<Facture, String> facture_date;

    @FXML
    private TableColumn<Facture, String> facture_nbr;

    public mainController(DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
    }

    @FXML
    public void initialize(){
        System.out.println("Hello");
        updateFactureTable();

    }
    @FXML
    public void updateFactureTable() {
        if (!facture_table.getItems().isEmpty()) {
            facture_table.getItems().clear();
        }

        List<Facture> factures = dependencyManager.getFactureRepository().findAll();
        ObservableList<Facture> observableFactures = FXCollections.observableArrayList(factures);
        facture_table.setItems(observableFactures);

        facture_id.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getId()).asString());
        facture_fournisseur.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFournisseur()));
        facture_date.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDate()).asString());
        facture_nbr.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDevise()));

    }

    @FXML
    protected void ajouterNouvelleFacture(ActionEvent e) throws IOException {
        Node source = (Node) e.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StokageApplication.class.getResource("nouvelleFacture.fxml"));
        fxmlLoader.setController(new nouvelleFactureController(dependencyManager));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.show();
    }
}
