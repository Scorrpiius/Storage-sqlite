package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class nouveauBonController {
    private String selectSortieId;
    @FXML
    private DatePicker dateBonPicker;
    @FXML
    private TextArea descMatPremASortir;
    @FXML
    private ComboBox<String> refMatPremASortir;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> sortiesTable;
    private final List<Map<String, Object>> listSorties;
    @FXML
    private TextField qteMatPremEnStock, qteMatPremASortir, idBonField;
    @FXML
    private TableColumn<Map<String, Object>, String> idSortieCol, refSortieCol, qteSortieCol, descrSortieCol;



    public nouveauBonController(DependencyManager dependencyManager){
        this.dependencyManager = dependencyManager;
        this.listSorties = new ArrayList<>();
    }
    public void initialize(){
        updateReference();
    }

    public void autoCompletion(ComboBox<String> comboBox, List<String> referencesList ){
        TextField textField = comboBox.getEditor();
        FilteredList<String> filteredItems = new FilteredList<>(FXCollections.observableArrayList(referencesList), p -> true);

        textField.textProperty().addListener((obs, oldValue, newValue) -> {
            final TextField editor = comboBox.getEditor();
            final String selected = comboBox.getSelectionModel().getSelectedItem();

            if (selected == null || !selected.equals(editor.getText())) {
                filterItems(filteredItems, newValue, comboBox);
                comboBox.show();
            }
        });
        comboBox.setItems(filteredItems);
    }
    private void filterItems(FilteredList<String> filteredItems, String filter, ComboBox<String> comboBox) {
        filteredItems.setPredicate(item -> {
            if (filter == null || filter.isEmpty()) {
                return true;
            }
            String lowerCaseFilter = filter.toLowerCase();
            return item.toLowerCase().contains(lowerCaseFilter);
        });
    }

    public void updateSortiesTable(){
        if (!sortiesTable.getItems().isEmpty()) {
            sortiesTable.getItems().clear();
        }

        ObservableList<Map<String, Object>> observableSorties = FXCollections.observableArrayList(listSorties);
        sortiesTable.setItems(observableSorties);

        idSortieCol.setCellValueFactory(cellData -> {
            int index = sortiesTable.getItems().indexOf(cellData.getValue());
            return new SimpleObjectProperty<>(String.valueOf(index));
        });
        refSortieCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_matierePremiere")).asString());
        qteSortieCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());
        descrSortieCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("description")).asString());

        sortiesTable.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    selectSortieId = (String) row.getItem().get("id_matierePremiere");
                    updateData(selectSortieId, event);
                }
            });
            return row;
        });
    }
    private void updateData(String selectSortieId, MouseEvent ignoredEvent) {
        Map<String, Object> s = listSorties.stream()
                .filter(map -> selectSortieId.equals(map.get("id_matierePremiere")))
                .findFirst()
                .get();
        Map<String, Object> ficheStock = dependencyManager.getFicheStockRepository().findById((String) s.get("id_matierePremiere"));

        String reference = (String) s.get("id_matierePremiere");
        String description = (String) s.get("description");
        String quantite = (String)s.get("quantite");
        int quantiteInit = Integer.parseInt(ficheStock.get("quantite").toString());

        this.refMatPremASortir.setValue(reference);
        this.descMatPremASortir.setText(description);
        this.qteMatPremASortir.setText(quantite);
        this.qteMatPremEnStock.setText(String.valueOf(quantiteInit));

    }
    public void updateReference(){
        List<Map<String, Object>> references = dependencyManager.getFicheStockRepository().getAllId();
        List<String> referencesList = new ArrayList<>();

        for(Map<String, Object> c : references){
            referencesList.add((String)c.get("id_matierePremiere"));
        }
        refMatPremASortir.setItems(FXCollections.observableArrayList(referencesList));
    }
    public void updateQuantite(ActionEvent ignoredEvent){
        Map<String, Object> ficheStock = dependencyManager.getFicheStockRepository().findById(refMatPremASortir.getValue());
        qteMatPremEnStock.setText(String.valueOf(ficheStock.get("quantite")));
    }


    public void ajouter(ActionEvent ignoredEvent) throws IOException, SQLException {
        String idMatPrem = refMatPremASortir.getValue();
        String quantite = qteMatPremASortir.getText();
        String description = this.descMatPremASortir.getText();

        boolean allFieldsFilled = !quantite.isEmpty() && idMatPrem != null && !description.isEmpty();

        if(!allFieldsFilled){
            accept();
            return;
        }

        int nouvelleQuantite = Integer.parseInt(qteMatPremEnStock.getText()) - Integer.parseInt(quantite);
        if (nouvelleQuantite < 0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Validation échouée");
            alert.setContentText("Quantité invalide");
            alert.showAndWait();
            return;
        }

        /* Vérifier que cette référence n'existe pas déjà dans la liste */
        Optional<Map<String, Object>> result = listSorties.stream()
                .filter(map -> idMatPrem.equals(map.get("id_matierePremiere")))
                .findFirst();

        if(result.isPresent()){
            System.out.println("Can't add the same reference twice");
            return;
        }

        Map<String, Object> s = new HashMap<>();
        s.put("quantite", quantite);
        s.put("id_matierePremiere", idMatPrem);
        s.put("description", description);
        listSorties.add(s);

        updateSortiesTable();
    }
    public void modifier(ActionEvent ignoredEvent) throws IOException {
        String idMatPrem = refMatPremASortir.getValue();
        String quantite = qteMatPremASortir.getText();
        String description = this.descMatPremASortir.getText();

        if(!selectSortieId.equals(idMatPrem)) {
            Optional<Map<String, Object>> result = listSorties.stream()
                    .filter(map -> idMatPrem.equals(map.get("id_matierePremiere")))
                    .findFirst();

            if (result.isPresent()) {
                System.out.println("Already exists in the table");
                return;
            }
        }

        /* Modifier les infos dans la liste */
        Map<String, Object> s = listSorties.stream()
                .filter(map -> selectSortieId.equals(map.get("id_matierePremiere")))
                .findFirst()
                .get();
        s.put("quantite", quantite);
        s.put("description", description);
        s.put("id_matierePremiere", idMatPrem);

        updateSortiesTable();
    }
    public void supprimerSortie(ActionEvent ignoredEvent){
        Optional<Map<String, Object>> s = listSorties.stream()
                .filter(map -> refMatPremASortir.getValue().equals(map.get("id_matierePremiere")))
                .findFirst();
        if(s.isEmpty()){
            System.out.println("Doesn't exist");
            return;
        } else {
            listSorties.remove(s.get());
            updateSortiesTable();
        }

    }
    public void finaliserSaisie(ActionEvent event) throws SQLException, IOException {
        final String idBonSortie = idBonField.getText();
        final LocalDate date = this.dateBonPicker.getValue();

        boolean allFieldsFilled = !idBonSortie.isEmpty() && date != null;

        if(!allFieldsFilled){
            accept();
            return;
        }

        if (!(dependencyManager.getBonSortieRepository().findById(idBonSortie) == null)){
            showAlert();
            return;
        }



        /* Création des sorties et mises à jour des fiches de stock */
        for(Map<String, Object> s : listSorties){
            /* Sorte */
            dependencyManager.getSortieRepository().create(
                    listSorties.indexOf(s),
                    idBonSortie,
                    s.get("id_matierePremiere").toString(),
                    s.get("description").toString(),
                    Integer.parseInt(s.get("quantite").toString())
            );

            /* Fiche de stock */
            Map<String, Object> ficheStock = dependencyManager.getFicheStockRepository().findById(s.get("id_matierePremiere").toString());
            int nouvelleQte = Integer.parseInt(ficheStock.get("quantite").toString()) - Integer.parseInt(s.get("quantite").toString());
            dependencyManager.getFicheStockRepository().updateQuantite(
                    s.get("id_matierePremiere").toString(),
                    nouvelleQte
            );

        }
        dependencyManager.getBonSortieRepository().create(idBonSortie, date);

        dependencyManager.getConnection().commit();
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }


    public void retour(ActionEvent event) throws IOException, SQLException {
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

    private static void accept() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Validation échouée");
        alert.setContentText("Veuillez remplir tous les champs");
        alert.showAndWait();
    }


}
