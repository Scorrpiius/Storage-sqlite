package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class modifierSortieController {
    private final String idBon;
    private String quantiteInit, referenceInit;
    @FXML
    private TextArea descArea;
    @FXML
    private ComboBox<String> referenceBox;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> sortiesTable;
    @FXML
    private TextField qte, qteStock, id;
    @FXML
    private TableColumn<Map<String, Object>, String> idColumn, refColumn, qteColumn, descColumn;

    public modifierSortieController(DependencyManager dependencyManager, String idBon){
        this.dependencyManager = dependencyManager;
        this.idBon = idBon;
    }

    public void initialize(){
        updateSortiesTable();
        referenceBox.setOnAction(e ->{
            String reference = referenceBox.getValue();
            updateQuantite(reference);
        });
    }

   public void updateQuantite(String ref){
        System.out.println(ref + " " + referenceInit);
        if(!ref.equals(referenceInit)){
            qteStock.setText(String.valueOf(dependencyManager.getFicheStockRepository().findById(ref).get("quantite")));
        } else {
            int quantite = Integer.parseInt(String.valueOf(dependencyManager.getFicheStockRepository().findById(ref).get("quantite"))) + Integer.parseInt(qte.getText());
            qteStock.setText(String.valueOf(quantite));
        }
    }

    @FXML
    public void updateSortiesTable(){
        if (!sortiesTable.getItems().isEmpty()) {
            sortiesTable.getItems().clear();
        }

        List<Map<String, Object>> sorties = dependencyManager.getSortieRepository().getAllSortiesByBon(idBon);
        ObservableList<Map<String, Object>> observableSorties = FXCollections.observableArrayList(sorties);
        sortiesTable.setItems(observableSorties);

        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id")).asString());
        refColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_matierePremiere")).asString());
        descColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("description")).asString());
        qteColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());

        sortiesTable.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    int idSortie = (int) row.getItem().get("id");
                    referenceInit = (String) row.getItem().get("id_matierePremiere");
                    updateData(idSortie, idBon, event);
                }
            });
            return row;
        });
    }

    @FXML
    public void updateReference(){
        List<Map<String, Object>> references = dependencyManager.getFicheStockRepository().getAllId();
        List<String> referencesList = new ArrayList<>();

        for (Map<String, Object> c : references) {
            referencesList.add((String) c.get("id_matierePremiere"));
        }
        referenceBox.setItems(FXCollections.observableArrayList(referencesList));
        referenceBox.setEditable(true);
    }

    public void updateData(int sortieId, String bonSortieId, MouseEvent event){
        updateReference();
        Map<String, Object> sortie = dependencyManager.getSortieRepository().findById(sortieId, bonSortieId);
        System.out.println(sortie);
        int quantite = (int) sortie.get("quantite");
        String reference = (String) sortie.get("id_matierePremiere");
        String description = (String) sortie.get("description");

        Map<String, Object> ficheStock = dependencyManager.getFicheStockRepository().findById(reference);

        int quantiteInitiale = (int) ficheStock.get("quantite") + quantite;

        descArea.setEditable(true);
        descArea.setText(description);

        referenceBox.setEditable(true);
        referenceBox.setValue(reference);

        qteStock.setText(String.valueOf(quantiteInitiale));
        qteStock.setEditable(true);

        qte.setEditable(true);
        qte.setText(String.valueOf(quantite));
        quantiteInit = String.valueOf(quantite);

        id.setText(String.valueOf(sortieId));
        id.setEditable(false);
    }

    public void valider(ActionEvent event) throws IOException, SQLException {
        final String reference = referenceBox.getValue();
        final String description = descArea.getText();
        final String quantite = qte.getText();
        final String id = this.id.getText();

        final boolean isValid = reference != null && description!= null  && quantite != null;

        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Validation échouée");
            alert.setContentText("Veuillez remplir tous les champs");
            alert.showAndWait();
            return;
        }

        dependencyManager.getSortieRepository().update(Integer.parseInt(id), idBon, Integer.parseInt(quantite), reference, description);
        int nouvelleQte = Integer.parseInt(qteStock.getText()) - Integer.parseInt(quantite);
        dependencyManager.getFicheStockRepository().update(reference, null, nouvelleQte, null, null);
        updateSortiesTable();
    }

    public void finir(ActionEvent event) throws IOException, SQLException {
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }
}
