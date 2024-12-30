package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public class produitFiniController {
    @FXML
    private AnchorPane root;
    @FXML
    private ImageView img;
    @FXML
    private Label titlePage;
    private final String produitID;
    @FXML
    private TextField referenceProduit;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> matierePremiereTable;
    @FXML
    private TableColumn<Map<String, Object>, String> refColumn, qteColumn;

    public produitFiniController(DependencyManager dependencyManager, String produitID, AnchorPane root) {
        this.dependencyManager = dependencyManager;
        this.produitID = produitID;
        this.root = root;
    }

    public void initialize(){
        titlePage.setText("Produit " + produitID);
        titlePage.setAlignment(Pos.CENTER);
        updateMatiereTable();
        updateData();
        telechargerImage();
        matierePremiereTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    @FXML
    public void telechargerImage(){
        dependencyManager.getProduitFiniRepository().getPicture(produitID, null, img);
    }
    @FXML
    public void updateData(){
        referenceProduit.setText(produitID);
        referenceProduit.setEditable(false);
    }
    @FXML
    public void updateMatiereTable(){
        if (!matierePremiereTable.getItems().isEmpty()) {
            matierePremiereTable.getItems().clear();
        }

        List<Map<String, Object>> matieresPremiere = dependencyManager.getProduitFiniMatierePremiereRepository().findByProduitId(produitID);
        if (matieresPremiere != null){
            ObservableList<Map<String, Object>> observableMatierePremieres = FXCollections.observableArrayList(matieresPremiere);
            matierePremiereTable.setItems(observableMatierePremieres);

            refColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("matiere_id")).asString());
            qteColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());
        }

    }

    public void retourAccueil(ActionEvent e) throws SQLException, IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilProduit.fxml"));
        fxmlLoader.setController(new accueilProduit(dependencyManager, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);
    }
    public void modifierProduit(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("modifierProduit.fxml"));
        fxmlLoader.setController(new modifierProduitFiniController(dependencyManager, produitID, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);
    }
    public void supprimerProduit(ActionEvent e) throws IOException, SQLException {
        String produitToDelete = referenceProduit.getText();
        dependencyManager.getProduitFiniMatierePremiereRepository().deleteAll(produitToDelete);
        dependencyManager.getProduitFiniRepository().delete(produitToDelete);
        dependencyManager.getConnection().commit();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilProduit.fxml"));
        fxmlLoader.setController(new accueilProduit(dependencyManager, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);


    }
}
