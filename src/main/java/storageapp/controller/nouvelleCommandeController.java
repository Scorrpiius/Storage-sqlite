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

public class nouvelleCommandeController {
    private String produitInit;
    @FXML
    private TextArea descriptionCommande;
    @FXML
    private DatePicker dateCommandePicker;
    @FXML
    private ComboBox<String> refProduitBox;
    private final DependencyManager dependencyManager;
    @FXML
    private TextField qteProduitField, idCommandeField;
    @FXML
    private TableView<Map<String, Object>> produitTable;
    private final List<Map<String, Object>> listeProduits;
    @FXML
    private TableColumn<Map<String, Object>, String> idProduitCol, qteProduitCol;


    public nouvelleCommandeController(DependencyManager dependencyManager){
        this.dependencyManager = dependencyManager;
        this.listeProduits = new ArrayList<>();
    }
    public void initialize(){
        updateProduit();
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

    public void updateProduit(){
        List<Map<String, Object>> produits = dependencyManager.getProduitFiniRepository().findAll();
        List<String> produitList = new ArrayList<>();

        for(Map<String, Object> c : produits){
            produitList.add((String)c.get("reference"));
        }
        refProduitBox.setItems(FXCollections.observableArrayList(produitList));
        refProduitBox.setEditable(true);

        autoCompletion(refProduitBox, produitList);
    }
    public void updateProduitTable(){
        if (!produitTable.getItems().isEmpty()) {
            produitTable.getItems().clear();
        }

        ObservableList<Map<String, Object>> observableProduits = FXCollections.observableArrayList(listeProduits);
        produitTable.setItems(observableProduits);

        idProduitCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_ProduitFini")).asString());
        qteProduitCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());

        produitTable.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    produitInit = (String) row.getItem().get("id_ProduitFini");
                    updateData(produitInit, event);
                }
            });
            return row;
        });
    }
    public void updateData(String produitId, MouseEvent ignoredEvent){
        Map<String, Object> p = listeProduits.stream()
                .filter(map -> produitId.equals(map.get("id_ProduitFini")))
                .findFirst()
                .get();

        refProduitBox.setValue(produitId);
        qteProduitField.setText(String.valueOf(p.get("quantite")));
    }
    public void ajouter(){
        String idProduit = refProduitBox.getValue();
        String quantite = qteProduitField.getText();

        boolean allFieldsFilled = !quantite.isEmpty() && idProduit != null;

        if(!allFieldsFilled){
            accept();
            return;
        }

        /* Vérifier que cette référence n'existe pas déjà dans la liste */
        Optional<Map<String, Object>> result = listeProduits.stream()
                .filter(map -> idProduit.equals(map.get("id_ProduitFini")))
                .findFirst();

        if(result.isPresent()){
            System.out.println("Can't add the same reference twice");
            return;
        }

        Map<String, Object> mp = new HashMap<>();
        mp.put("id_ProduitFini", idProduit);
        mp.put("quantite", quantite);

        listeProduits.add(mp);
        refProduitBox.setValue("");
        qteProduitField.setText("");
        updateProduitTable();

    }
    public void modifier(){
        String newProduit = refProduitBox.getValue();
        String quantite = qteProduitField.getText();

        if (produitInit == null){
            System.out.println("Click on the line to modify");
            return;
        }

        if(!produitInit.equals(newProduit)) {
            Optional<Map<String, Object>> result = listeProduits.stream()
                    .filter(map -> newProduit.equals(map.get("id_ProduitFini")))
                    .findFirst();

            if (result.isPresent()) {
                System.out.println("Already exists in the table");
                return;
            }
        }

        Map<String, Object> p = listeProduits.stream()
                .filter(map -> produitInit.equals(map.get("id_ProduitFini")))
                .findFirst()
                .get();
        p.put("id_ProduitFini", newProduit);
        p.put("quantite", quantite);

        refProduitBox.setValue("");
        qteProduitField.setText("");
        updateProduitTable();
    }
    public void supprimerProduit(){
        Optional<Map<String, Object>> p = listeProduits.stream()
                .filter(map -> refProduitBox.getValue().equals(map.get("id_ProduitFini")))
                .findFirst();
        if(p.isEmpty()){
            System.out.println("Doesn't exist");
            return;
        } else {
            listeProduits.remove(p.get());
            updateProduitTable();
        }
    }
    @FXML
    public void finaliserSaisie(ActionEvent e) throws SQLException {
        final String idCommande = idCommandeField.getText();
        final String descriptionCommande = this.descriptionCommande.getText();
        final LocalDate dateCommande = dateCommandePicker.getValue();

        boolean allFieldsFilled = !idCommande.isEmpty()
                && !descriptionCommande.isEmpty()
                && dateCommande != null;

        if(!allFieldsFilled){
            accept();
            return;
        }

        if (!(dependencyManager.getCommandeRepository().findById(idCommande) == null)){
            showAlert();
            return;
        }

        dependencyManager.getCommandeRepository().create(idCommande, descriptionCommande, dateCommande);

        for(Map<String, Object> p : listeProduits){
            dependencyManager.getCommandeProduitFiniRepository().create(idCommande, p.get("id_ProduitFini").toString(), Integer.parseInt(p.get("quantite").toString()));
        }

        dependencyManager.getConnection().commit();
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

    }
    public void retour(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Validation échouée");
        alert.setContentText("Cette référence de commande existe déjà. Veuillez saisir une nouvelle référence");
        alert.showAndWait();
    }
    private static void accept() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Validation échouée");
        alert.setContentText("Veuillez remplir tous les champs");
        alert.showAndWait();
        return;
    }
}
