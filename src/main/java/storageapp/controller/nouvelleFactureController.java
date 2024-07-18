package stockapp.controller;

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
import stockapp.StokageApplication;
import stockapp.model.Entree;
import stockapp.service.DependencyManager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class nouvelleFactureController {
    @FXML
    private DatePicker dateField;
    @FXML
    private TextField idField;
    @FXML
    private TextField fournisseurField;
    @FXML
    private TextField deviseField;
    @FXML
    private TextField txTheoField;
    @FXML
    private TextField txReelField;
    private final DependencyManager dependencyManager;
    private Stage stage;

    @FXML
    private TableView<Entree> entreeTable;
    @FXML
    private TableColumn<Entree, String> entreeRef;
    @FXML
    private TableColumn<Entree, String> entreeDes;
    @FXML
    private TableColumn<Entree, Double> entreeQte;
    @FXML
    private TableColumn<Entree, Double> entreePxUnit;

    public nouvelleFactureController(DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
    }
    /*public void initialize(){
        updateEntreeTable();
    }
    public void updateEntreeTable(){
        if (!entreeTable.getItems().isEmpty()) {
            entreeTable.getItems().clear();
        }

        String idText = idField.getText();
        if (!idText.isEmpty()) { // Vérifier si le champ de texte n'est pas vide
            try {
                long factureId = Long.parseLong(idText);
                List<Entree> entrees = dependencyManager.getFactureRepository().findById(factureId).getListeEntree();
                ObservableList<Entree> observableEntrees = FXCollections.observableArrayList(entrees);
                entreeTable.setItems(observableEntrees);

                entreeRef.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getReference()));
                entreeDes.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getDesignation()).asString());
                entreeQte.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getQuantite()));
                entreePxUnit.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPx_unit_init()));
            } catch (NumberFormatException e) {
                // Gérer le cas où l'ID de la facture n'est pas un nombre valide
                System.err.println("L'ID de la facture n'est pas un nombre valide : " + idText);
            }
        } else {
            // Gérer le cas où le champ de texte est vide
            System.err.println("Le champ de texte de l'ID de la facture est vide.");
        }
    }*/

    public void ajouterMatierePremiere(ActionEvent event) throws IOException {
        /*final String id = idField.getText();
        final LocalDate date = dateField.getValue();
        final String devise = deviseField.getText();
        final String tx_reel = txReelField.getText();

        final boolean isValid = id != null && date != null  && devise != null && tx_reel != null ;

        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Validation échouée");
            alert.setContentText("Veuillez remplir tous les champs avant de rajouter une entrée");
            alert.showAndWait();
            return;
        }

        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StokageApplication.class.getResource("nouvelleEntree.fxml"));
        fxmlLoader.setController(new nouvelleEntreeController(dependencyManager, date, id));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = new Stage();
        stage.setTitle("Stockage");
        stage.setScene(scene);
        stage.show();*/
    }

    public void finaliserSaisie(ActionEvent event) throws IOException {
        final String id = idField.getText();
        final String fournisseur = fournisseurField.getText();
        final String devise = deviseField.getText();
        final double tx_reel = Double.parseDouble(txReelField.getText());
        final double tx_theo = Double.parseDouble(txTheoField.getText());
        final LocalDate date = dateField.getValue();


        final boolean isValid = id != null && fournisseur != null  && devise != null && date != null;

        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Validation échouée");
            alert.setContentText("Veuillez remplir tous les champs");
            alert.showAndWait();
            return;
        }

        dependencyManager.getFactureRepository().create(Long.parseLong(id), date, fournisseur,tx_theo, tx_reel, devise, null);

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setController(new mainController(dependencyManager));
        fxmlLoader.setLocation(StokageApplication.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage = new Stage();
        stage.setTitle("Stockapp");
        stage.setScene(scene);
        stage.show();
    }

}
