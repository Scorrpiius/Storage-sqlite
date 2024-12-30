package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class modifierCommandeController {
    @FXML
    private AnchorPane root;
    @FXML
    private Label titlePage;
    private final String IDCOMMANDE;
    @FXML
    private TextArea descriptionCommande;
    @FXML
    private DatePicker dateCommandePicker;
    @FXML
    private ComboBox<String> refProduitBox;
    private String idProduitInit, idCommandeInit;
    private final DependencyManager dependencyManager;
    @FXML
    private TextField qteProduitField, idCommandeField;
    @FXML
    private TableView<Map<String, Object>> produitTable;
    @FXML
    private TableColumn<Map<String, Object>, String> idProduitCol, qteProduitCol;


    public modifierCommandeController(DependencyManager dependencyManager, String commandeId, AnchorPane root){
        this.dependencyManager = dependencyManager;
        this.idCommandeInit = commandeId;
        this.IDCOMMANDE = commandeId;
        this.root = root;
    }
    public void initialize(){
        titlePage.setText("Modifier la commande N° "+ IDCOMMANDE);
        updateProduit();
        updateProduitTable();
        updateAllData();
        produitTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    public void updateAllData(){
        Map<String, Object> commande = dependencyManager.getCommandeRepository().findById(idCommandeInit);
        idCommandeField.setText(idCommandeInit);
        dateCommandePicker.setValue(LocalDate.parse(commande.get("date").toString()));
        descriptionCommande.setText((String) commande.get("description"));
    }
    public void updateProduit(){
        List<Map<String, Object>> produits = dependencyManager.getProduitFiniRepository().findAll();
        List<String> produitList = new ArrayList<>();

        for(Map<String, Object> c : produits){
            produitList.add((String)c.get("reference"));
        }
        refProduitBox.setItems(FXCollections.observableArrayList(produitList));
        refProduitBox.setEditable(true);
        TextFields.bindAutoCompletion(refProduitBox.getEditor(), refProduitBox.getItems());
    }
    public void updateProduitTable(){
        if (!produitTable.getItems().isEmpty()) {
            produitTable.getItems().clear();
        }

        List<Map<String, Object>> produits = dependencyManager.getCommandeProduitFiniRepository().findByCommandeId(idCommandeInit);
        ObservableList<Map<String, Object>> observableProduits = FXCollections.observableArrayList(produits);
        produitTable.setItems(observableProduits);

        idProduitCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_ProduitFini")).asString());
        qteProduitCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());

        produitTable.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    idProduitInit = (String) row.getItem().get("id_ProduitFini");
                    updateData(idProduitInit, event);
                }
            });
            return row;
        });
    }
    public void updateData(String produitId, MouseEvent ignoredEvent){
        Map<String, Object> produit = dependencyManager.getCommandeProduitFiniRepository().findByIds(idCommandeInit, produitId);
        int quantite = (int) produit.get("quantite");
        refProduitBox.setValue(produitId);
        qteProduitField.setText(String.valueOf(quantite));
    }
    public void retourCommande(ActionEvent event) throws IOException, SQLException {
        dependencyManager.getConnection().rollback();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("commande.fxml"));
        fxmlLoader.setController(new commandeController(dependencyManager, idCommandeInit, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);
    }
    public void finaliserModifCommande(ActionEvent e) throws SQLException, IOException {
        if(!IDCOMMANDE.equals(idCommandeField.getText())){
            if (!(dependencyManager.getCommandeRepository().findById(idCommandeField.getText()) == null)){
                showAlert();
                return;
            }
        }

        majId();
        dependencyManager.getCommandeRepository().updateInfos(idCommandeInit, descriptionCommande.getText(), dateCommandePicker.getValue());
        dependencyManager.getConnection().commit();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("commande.fxml"));
        fxmlLoader.setController(new commandeController(dependencyManager, idCommandeInit, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);

    }
    public void modifierProduit(){
        majId();

        String newProduit = refProduitBox.getValue();
        String quantite = qteProduitField.getText();
        dependencyManager.getCommandeProduitFiniRepository().update(idCommandeInit, idProduitInit, newProduit, quantite);
        refProduitBox.setValue("");
        qteProduitField.setText("");
        updateProduitTable();    }
    public void supprimerProduit(){
        majId();

        String produitToDelete = refProduitBox.getValue();
        /* Supprimer le lien produit commande */
        dependencyManager.getCommandeProduitFiniRepository().delete(idCommandeInit, produitToDelete);
        updateProduitTable();
    }
    public void ajouterProduit(){
        majId();

        String idProduit = refProduitBox.getValue();
        String quantite = qteProduitField.getText();

        dependencyManager.getCommandeProduitFiniRepository().create(idCommandeInit, idProduit, Integer.parseInt(quantite));

        refProduitBox.setValue("");
        qteProduitField.setText("");
        updateProduitTable();    }
    public void majId(){
        String idCommandeNouveau = idCommandeField.getText();

        /* Si les id sont différents faire la maj sinon ne rien faire */
        if (!idCommandeNouveau.equals(idCommandeInit)){

            /* MAJ */
            dependencyManager.getCommandeRepository().updateCommandeId(idCommandeInit, idCommandeNouveau);
            if (!(dependencyManager.getCommandeProduitFiniRepository().findByCommandeId(idCommandeInit) == null)) {
                dependencyManager.getCommandeProduitFiniRepository().updateCommandeId(idCommandeInit, idCommandeNouveau);
            }
        }
        idCommandeInit = idCommandeNouveau;
    }
    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Validation échouée");
        alert.setContentText("Cette référence de commande existe déjà. Veuillez saisir une nouvelle référence");
        alert.showAndWait();
    }
}
