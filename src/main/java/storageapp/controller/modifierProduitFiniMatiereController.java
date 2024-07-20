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

import java.util.Map;
import java.util.List;

public class modifierProduitFiniMatiereController {
    private String produitId;
    private final DependencyManager dependencyManager;
    @FXML
    private TextField qte;
    @FXML
    private ComboBox<String> referenceBox;
    @FXML
    private TableView<Map<String, Object>> matierePremiereTable;
    @FXML
    private TableColumn<Map<String, Object>, String> refColumn, qteColumn;

    public modifierProduitFiniMatiereController(DependencyManager dependencyManager, String produitId){
        this.dependencyManager = dependencyManager;
        this.produitId = produitId;
    }

    public void initialize(){
        updateMatiereTable();
    }

    public void updateMatiereTable(){
        if (!matierePremiereTable.getItems().isEmpty()) {
            matierePremiereTable.getItems().clear();
        }

        List<Map<String, Object>> matieres = dependencyManager.getProduitFiniMatierePremiereRepository().findByProduitId(produitId);
        ObservableList<Map<String, Object>> observableMatieres = FXCollections.observableArrayList(matieres);
        matierePremiereTable.setItems(observableMatieres);

        refColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("matiere_id")).asString());
        qteColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());

        matierePremiereTable.setRowFactory(tv -> {
            TableRow<Map<String, Object>> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    String matiereId = (String) row.getItem().get("matiere_id");
                    updateData(matiereId, event);
                }
            });
            return row;
        });
    }

    public void updateData(String matiereId, MouseEvent e){
        referenceBox.setValue(matiereId);
        qte.setText(String.valueOf(dependencyManager.getProduitFiniMatierePremiereRepository().findByIds(produitId, matiereId).get("quantite")));
    }
    public void supprimerMatPrem(ActionEvent e){
        String matiereId = referenceBox.getValue();
        dependencyManager.getProduitFiniMatierePremiereRepository().delete(produitId,matiereId);
        referenceBox.setValue("");
        qte.setText("");
        updateMatiereTable();
    }
    public void finirModifications(ActionEvent e){
        Node source = (Node) e.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }
    public void validerModifications(ActionEvent e){
        int nouvelleQte = Integer.parseInt(qte.getText());
        String matiereId = referenceBox.getValue();
        dependencyManager.getProduitFiniMatierePremiereRepository().updateQuantite(produitId, matiereId,nouvelleQte);
        updateMatiereTable();

    }
}
