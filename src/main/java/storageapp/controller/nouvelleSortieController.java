package storageapp.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import storageapp.service.DependencyManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class nouvelleSortieController {
    private final int id;
    private final String idBon;
    @FXML
    private TextArea description;
    @FXML
    private ComboBox<String> referenceBox;
    @FXML
    private TextField qteStock, qte;
    private final DependencyManager dependencyManager;

    public nouvelleSortieController(DependencyManager dependencyManager, int id, String idBon){
        this.dependencyManager = dependencyManager;
        this.id = id;
        this.idBon = idBon;
    }

    public void initialize(){
        qteStock.setEditable(false);
        updateReference();
    }
    @FXML
    public void updateReference(){
        List<Map<String, Object>> references = dependencyManager.getFicheStockRepository().getAllId();
        List<String> referencesList = new ArrayList<>();

        for(Map<String, Object> c : references){
            referencesList.add((String)c.get("id_matierePremiere"));
        }
        referenceBox.setItems(FXCollections.observableArrayList(referencesList));
        referenceBox.setEditable(true);
    }
    @FXML
    public void updateQuantite(ActionEvent event){
        Map<String, Object> ficheStock = dependencyManager.getFicheStockRepository().findById(referenceBox.getValue());
        qteStock.setText(String.valueOf(ficheStock.get("quantite")));
    }
    public void finirSaisie(ActionEvent event) throws SQLException {
        String idMatPrem = referenceBox.getValue();
        String quantite = qte.getText();
        String description = this.description.getText();
        int nouvelleQuantite = Integer.parseInt(qteStock.getText()) - Integer.parseInt(quantite);

        if (nouvelleQuantite < 0){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setHeaderText("Validation échouée");
            alert.setContentText("Quantité invalide");
            alert.showAndWait();
            return;
        }

        dependencyManager.getSortieRepository().create(this.id, idBon, idMatPrem, description, Integer.parseInt(quantite));
        dependencyManager.getFicheStockRepository().update(idMatPrem, null,nouvelleQuantite,null,null);

        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
