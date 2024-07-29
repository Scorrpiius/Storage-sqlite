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
    int id, idInit;
    private final String IDBON;
    @FXML
    private DatePicker dateBonPicker;
    @FXML
    private TextArea descMatPremASortir;
    private String selectSortieId, idBonInit;
    @FXML
    private ComboBox<String> refMatPremASortir;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> sortiesTable;
    @FXML
    private TextField qteMatPremEnStock, qteMatPremASortir, idBonField ;
    @FXML
    private TableColumn<Map<String, Object>, String> idSortieCol, refSortieCol, qteSortieCol, descrSortieCol;

    public modifierBonController(DependencyManager dependencyManager, String bonSortieId){
        this.dependencyManager = dependencyManager;
        this.idBonInit = bonSortieId;
        this.IDBON = bonSortieId;
    }

    public void getLastId(){
        List<Map<String, Object>> sorties = dependencyManager.getSortieRepository().getAllSortiesByBon(idBonInit);
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
    public void updateReference(){
        List<Map<String, Object>> references = dependencyManager.getFicheStockRepository().getAllId();
        List<String> referencesList = new ArrayList<>();

        for(Map<String, Object> c : references){
            referencesList.add((String)c.get("id_matierePremiere"));
        }
        refMatPremASortir.setItems(FXCollections.observableArrayList(referencesList));
        refMatPremASortir.setEditable(true);
    }
    public void updateQuantite(ActionEvent ignoredevent){
        Map<String, Object> ficheStock = dependencyManager.getFicheStockRepository().findById(refMatPremASortir.getValue());
        qteMatPremEnStock.setText(String.valueOf(ficheStock.get("quantite")));
    }

    public void updateData(){
        Map<String, Object> bonSortie = dependencyManager.getBonSortieRepository().findById(idBonInit);
        idBonField.setEditable(true);
        dateBonPicker.setEditable(true);

        idBonField.setText(String.valueOf(bonSortie.get("id")));
        dateBonPicker.setValue(LocalDate.parse(bonSortie.get("date").toString()));
    }
    public void updateSortieTable(){
        if (!sortiesTable.getItems().isEmpty()) {
            sortiesTable.getItems().clear();
        }

        List<Map<String, Object>> sorties = dependencyManager.getSortieRepository().getAllSortiesByBon(String.valueOf(idBonInit));
        ObservableList<Map<String, Object>> observableSorties = FXCollections.observableArrayList(sorties);
        sortiesTable.setItems(observableSorties);

        idSortieCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id")).asString());
        refSortieCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_matierePremiere")).asString());
        qteSortieCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());
        descrSortieCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("description")).asString());

        sortiesTable.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    idInit = Integer.parseInt(row.getItem().get("id").toString());
                    selectSortieId = row.getItem().get("id_matierePremiere").toString();
                    updateData(selectSortieId, event);
                }
            });
            return row;
        });

    }
    private void updateData(String selectSortieId, MouseEvent ignoredevent) {
        majId();
        Map<String, Object> sortie = dependencyManager.getSortieRepository().findByIds(selectSortieId, idBonInit);
        Map<String, Object> ficheStock = dependencyManager.getFicheStockRepository().findById((String) sortie.get("id_matierePremiere"));

        String reference = (String) sortie.get("id_matierePremiere");
        String description = (String) sortie.get("description");
        int quantite = (int) sortie.get("quantite");
        int quantiteStockInit = (int) ficheStock.get("quantite");

        this.refMatPremASortir.setValue(reference);
        this.descMatPremASortir.setText(description);
        this.qteMatPremASortir.setText(String.valueOf(quantite));
        this.qteMatPremEnStock.setText(String.valueOf(quantiteStockInit));

    }


    public void ajouterSortie(ActionEvent ignoredevent){
        majId();

        String idMatPrem = refMatPremASortir.getValue();
        String quantite = qteMatPremASortir.getText();
        String description = this.descMatPremASortir.getText();
        int nouvelleQuantite = Integer.parseInt(qteMatPremEnStock.getText()) - Integer.parseInt(quantite);

        if (nouvelleQuantite < 0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Validation échouée");
            alert.setContentText("Quantité invalide");
            alert.showAndWait();
            return;
        }
        /* Retirer la quantité de la fiche de stock */
        dependencyManager.getFicheStockRepository().updateQuantite(idMatPrem, nouvelleQuantite);

        /* Créer la sortie de la matière première */
        dependencyManager.getSortieRepository().create(this.id, idBonInit, idMatPrem, description, Integer.parseInt(quantite));
        updateSortieTable();
        id++;
    }
    public void supprimerSortie() {
        majId();

        /* Supprimer la sortie associé */
        String idMatPremiere = refMatPremASortir.getValue();
        dependencyManager.getSortieRepository().delete(idMatPremiere, idBonInit);

        /* Rajouter la quantité supprimée */
        String quantiteSortie = qteMatPremASortir.getText();
        String quantiteStock = qteMatPremEnStock.getText();
        int nouvelleQuantite = Integer.parseInt(quantiteStock) + Integer.parseInt(quantiteSortie);
        dependencyManager.getFicheStockRepository().updateQuantite(idMatPremiere, nouvelleQuantite);
        updateSortieTable();
    }
    public void modifier(ActionEvent ignoredevent){
        majId();

        /* Modifier la fiche de stock */
        int quantiteSortie, nouvelleQuantiteSortir, nouvelleQtStock, quantiteStock;
        String idMatPrem = refMatPremASortir.getValue();
        String quantite = qteMatPremASortir.getText();
        String description = this.descMatPremASortir.getText();

        quantiteSortie = Integer.parseInt(dependencyManager.getSortieRepository().findByIds(idMatPrem, idBonInit).get("quantite").toString());
        nouvelleQuantiteSortir = Integer.parseInt(quantite);
        quantiteStock = Integer.parseInt(dependencyManager.getFicheStockRepository().findById(idMatPrem).get("qauntite").toString());

        nouvelleQtStock = quantiteStock + quantiteSortie - nouvelleQuantiteSortir;
        dependencyManager.getFicheStockRepository().updateQuantite(idMatPrem, nouvelleQtStock);
        dependencyManager.getSortieRepository().update(idInit,idBonField.getText(), Integer.parseInt(quantite),idMatPrem,description);
        updateSortieTable();
    }
    public void terminer(ActionEvent event) throws SQLException {
        if(!IDBON.equals(idBonField.getText())){
            if (!(dependencyManager.getBonSortieRepository().findById(idBonField.getText()) == null)){
                showAlert();
                return;
            }
        }

        majId();
        final LocalDate date = this.dateBonPicker.getValue();
        final boolean isValid = date != null;

        if (!isValid) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Validation échouée");
            alert.setContentText("Veuillez remplir tous les champs");
            alert.showAndWait();
            return;
        }

        dependencyManager.getBonSortieRepository().updateDate(idBonInit, date);

        dependencyManager.getConnection().commit();
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public void majId(){
        String idBonNouveau = idBonField.getText();

        /* Si les id sont différents faire la maj sinon ne rien faire */
        if (!idBonNouveau.equals(idBonInit)){

            /* MAJ */
            dependencyManager.getBonSortieRepository().updateBonId(idBonInit, idBonNouveau);
            if (!(dependencyManager.getSortieRepository().getAllSortiesByBon(idBonNouveau) == null)){
                dependencyManager.getSortieRepository().updateBonId(idBonInit, idBonNouveau);
            }
        }
        idBonInit = idBonNouveau;
    }
    public void retour(ActionEvent event) throws SQLException {
        dependencyManager.getConnection().rollback();
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Validation échouée");
        alert.setContentText("Cette référence de bon existe déjà. Veuillez saisir une nouvelle référence");
        alert.showAndWait();
    }

}
