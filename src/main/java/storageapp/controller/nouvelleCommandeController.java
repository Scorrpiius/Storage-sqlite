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
import org.w3c.dom.Text;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class nouvelleCommandeController {
    @FXML
    private AnchorPane root;
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


    public nouvelleCommandeController(DependencyManager dependencyManager, AnchorPane root){
        this.dependencyManager = dependencyManager;
        this.listeProduits = new ArrayList<>();
        this.root = root;
    }
    public void initialize(){
        updateProduit();
        produitTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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

    public void ajouterProduit(){
        String idProduit = refProduitBox.getValue();
        String quantite = qteProduitField.getText();

        if (assertProduit(idProduit, quantite, "add")){return;};

        Map<String, Object> mp = new HashMap<>();
        mp.put("id_ProduitFini", idProduit);
        mp.put("quantite", quantite);

        listeProduits.add(mp);
        refProduitBox.setValue("");
        qteProduitField.setText("");
        updateProduitTable();

    }
    public void modifierProduit(){
        String newProduit = refProduitBox.getValue();
        String quantite = qteProduitField.getText();

        if (assertProduit(newProduit, quantite, "edit")){return;};

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

    public void finaliserCommande(ActionEvent e) throws SQLException, IOException {
        final String idCommande = idCommandeField.getText();
        final String descriptionCommande = this.descriptionCommande.getText();
        final LocalDate dateCommande = dateCommandePicker.getValue();

        if(assertCommande(idCommande, descriptionCommande, dateCommande)){return;}
        dependencyManager.getCommandeRepository().create(idCommande, descriptionCommande, dateCommande);

        for(Map<String, Object> p : listeProduits){
            dependencyManager.getCommandeProduitFiniRepository().create(idCommande, p.get("id_ProduitFini").toString(), Integer.parseInt(p.get("quantite").toString()));
        }

        dependencyManager.getConnection().commit();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilCommande.fxml"));
        fxmlLoader.setController(new accueilCommande(dependencyManager, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);

    }
    public void retourAccueil(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilCommande.fxml"));
        fxmlLoader.setController(new accueilCommande(dependencyManager, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);
    }

    public boolean assertProduit(String idProduit, String quantite, String status){
        if (status.equals("add")){
            boolean allFieldsFilled = !quantite.isEmpty() && idProduit != null;

            if(!allFieldsFilled){
                showAlert("Veuillez remplir tous les champs");
                return true;
            }

            /* Vérifier que cette référence n'existe pas déjà dans la liste */
            Optional<Map<String, Object>> result = listeProduits.stream()
                    .filter(map -> idProduit.equals(map.get("id_ProduitFini")))
                    .findFirst();

            if(result.isPresent()){
                showAlert("Cette référence de produit est déjà présente dans la liste, si vous souhaitez modifier la quantité, appuyez sur le bouton modifier. " +
                        "Sinon veuillez choisir un autre produit");
                return true;
            }
        } else if (status.equals("edit")){
            if (produitInit == null){
                showAlert("Choisissez un produit à modifier");
                return true;
            }

            if(!produitInit.equals(idProduit)) {
                Optional<Map<String, Object>> result = listeProduits.stream()
                        .filter(map -> idProduit.equals(map.get("id_ProduitFini")))
                        .findFirst();

                if (result.isPresent()) {
                    showAlert("Cette référence de produit existe déjà dans la table");
                    return true;
                }
            }
        }
        return false;
    }
    public boolean assertCommande(String idCommande, String descriptionCommande, LocalDate dateCommande){
        boolean allFieldsFilled = !idCommande.isEmpty()
                && !descriptionCommande.isEmpty()
                && dateCommande != null;

        if(!allFieldsFilled){
            showAlert("Veuillez remplir tous les champs");
            return true;
        }

        if (!(dependencyManager.getCommandeRepository().findById(idCommande) == null)){
            showAlert("Cette référence de commande existe déjà, veuillez en saisir une nouvelle.");
            return true;
        }
        return false;
    }
    public void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Validation échouée");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
