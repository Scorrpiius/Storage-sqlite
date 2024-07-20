package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import storageapp.service.DependencyManager;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class modifierBonController {
    int selectSortieId, id;
    @FXML
    private Label titlePage;
    @FXML
    private TextField idBonField;
    @FXML
    private ComboBox<String> refMatPremSortir;
    @FXML
    private TextArea descMatSortir;
    @FXML
    private DatePicker dateBon;
    private DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> sortiesTableau;
    @FXML
    private TextField qteMatStock, qteMatSortir ;
    @FXML
    private TableColumn<Map<String, Object>, String> idColumn, refColumn, qteColumn, descColumn;
    private final String bonSortieId;
    private String quantiteInit;
    public modifierBonController(DependencyManager dependencyManager, String bonSortieId){
        this.dependencyManager = dependencyManager;
        this.bonSortieId = bonSortieId;
    }

    public void getLastId(){
        List<Map<String, Object>> sorties = dependencyManager.getSortieRepository().getAllSortiesByBon(bonSortieId);
        for(Map<String, Object> sortie : sorties){
            this.id = (int) sortie.get("id");
        }
        this.id++;
    }
    public void initialize(){
        updateSortieTable();
        updateData();
        updateReference();
        getLastId();
    }

    @FXML
    public void updateReference(){
        List<Map<String, Object>> references = dependencyManager.getFicheStockRepository().getAllId();
        List<String> referencesList = new ArrayList<>();

        for(Map<String, Object> c : references){
            referencesList.add((String)c.get("id_matierePremiere"));
        }
        refMatPremSortir.setItems(FXCollections.observableArrayList(referencesList));
        refMatPremSortir.setEditable(true);
    }

    @FXML
    public void updateQuantite(ActionEvent event){
        Map<String, Object> ficheStock = dependencyManager.getFicheStockRepository().findById(refMatPremSortir.getValue());
        qteMatStock.setText(String.valueOf(ficheStock.get("quantite")));
    }

    @FXML
    public void updateData(){
        Map<String, Object> bonSortie = dependencyManager.getBonSortieRepository().findById(bonSortieId);
        idBonField.setEditable(false);
        dateBon.setEditable(false);

        idBonField.setText(String.valueOf(bonSortie.get("id")));
        dateBon.setValue(LocalDate.parse(bonSortie.get("date").toString()));
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

        sortiesTableau.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    selectSortieId = (int) row.getItem().get("id");
                    updateData(selectSortieId, event);
                }
            });
            return row;
        });

    }

    @FXML
    public void ajouterSortie(ActionEvent event){
        String idMatPrem = refMatPremSortir.getValue();
        String quantite = qteMatSortir.getText();
        String description = this.descMatSortir.getText();
        int nouvelleQuantite = Integer.parseInt(qteMatStock.getText()) - Integer.parseInt(quantite);

        if (nouvelleQuantite < 0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Validation échouée");
            alert.setContentText("Quantité invalide");
            alert.showAndWait();
            return;
        }

        dependencyManager.getSortieRepository().create(this.id, idBonField.getText(), idMatPrem, description, Integer.parseInt(quantite));
        updateSortieTable();
        id++;
    }
    @FXML
    private void updateData(int selectSortieId, MouseEvent event) {
        Map<String, Object> sortie = dependencyManager.getSortieRepository().findById(selectSortieId, idBonField.getText());
        Map<String, Object> ficheStock = dependencyManager.getFicheStockRepository().findById((String) sortie.get("id_matierePremiere"));

        String reference = (String) sortie.get("id_matierePremiere");
        String description = (String) sortie.get("description");
        int quantite = (int) sortie.get("quantite");
        int quantiteStockInit = (int) ficheStock.get("quantite");

        this.refMatPremSortir.setValue(reference);
        this.descMatSortir.setText(description);
        this.qteMatSortir.setText(String.valueOf(quantite));
        this.qteMatStock.setText(String.valueOf(quantiteStockInit));
        quantiteInit = String.valueOf(quantite);


    }
    @FXML
    public void supprimerSortie() throws SQLException {
        String idSortie = String.valueOf(selectSortieId);
        String idBonSortie = idBonField.getText();
        dependencyManager.getSortieRepository().delete(idSortie, idBonSortie);

        // Mettre à jour la fiche de stock
        String quantiteSortie = qteMatSortir.getText();
        String refMatPremiere = refMatPremSortir.getValue();
        String quantiteStock = qteMatStock.getText();
        int nouvelleQuantite = Integer.parseInt(quantiteStock) + Integer.parseInt(quantiteSortie);
        dependencyManager.getFicheStockRepository().update(refMatPremiere, refMatPremiere, nouvelleQuantite, null, null);
        updateSortieTable();
    }
    @FXML
    public void retour(ActionEvent event) throws SQLException {
        dependencyManager.getConnection().rollback();
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }
    @FXML
    public void terminer(ActionEvent event) throws SQLException {
        final LocalDate date = this.dateBon.getValue();
        final boolean isValid = idBonField.getText()!= null && date != null;

        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Validation échouée");
            alert.setContentText("Veuillez remplir tous les champs");
            alert.showAndWait();
            return;
        }

        List<Map<String, Object>> sorties = dependencyManager.getSortieRepository().getAllSortiesByBon(bonSortieId);
        for(Map<String, Object> sortie : sorties){
            int qte = (int) sortie.get("quantite");
            String refMatierePremiere = (String) sortie.get("id_matierePremiere");

            Map<String, Object> ficheStock = dependencyManager.getFicheStockRepository().findById(refMatierePremiere);
            int nouvelleQte = (int) ficheStock.get("quantite") - qte;

            dependencyManager.getFicheStockRepository().update(refMatierePremiere, null, nouvelleQte, null, null);
        }
        dependencyManager.getBonSortieRepository().update(bonSortieId, idBonField.getText(), date);
        dependencyManager.getSortieRepository().update(bonSortieId,idBonField.getText());

        dependencyManager.getConnection().commit();
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void modifier(ActionEvent event){
        String idMatPrem = refMatPremSortir.getValue();
        String quantite = qteMatSortir.getText();
        String description = this.descMatSortir.getText();


        dependencyManager.getSortieRepository().update(selectSortieId,idBonField.getText(), Integer.parseInt(quantite),idMatPrem,description);
        updateSortieTable();
    }

}
