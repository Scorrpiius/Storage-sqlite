package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import storageapp.service.DependencyManager;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class nouvelleCommandeController {
    private final DependencyManager dependencyManager;
    @FXML
    private ComboBox<String> produitBox;
    @FXML
    private TextField produitQuantite, id;
    @FXML
    private TextArea description;
    @FXML
    private DatePicker date;
    @FXML
    private TableView<Map<String, Object>> produitTable;
    @FXML
    private TableColumn<Map<String, Object>, String> idColumn, qteColumn;




    public nouvelleCommandeController(DependencyManager dependencyManager){
        this.dependencyManager = dependencyManager;
    }

    public void initialize(){
        updateProduit();
    }

    public void updateProduit(){
        List<Map<String, Object>> produits = dependencyManager.getProduitFiniRepository().findAll();
        List<String> produitList = new ArrayList<>();

        for(Map<String, Object> c : produits){
            produitList.add((String)c.get("reference"));
        }
        produitBox.setItems(FXCollections.observableArrayList(produitList));
        produitBox.setEditable(true);
    }

    public void updateProduitTable(){
        if (!produitTable.getItems().isEmpty()) {
            produitTable.getItems().clear();
        }
        String idCommande = id.getText();

        List<Map<String, Object>> produits = dependencyManager.getCommandeProduitFiniRepository().findByCommandeId(idCommande);
        ObservableList<Map<String, Object>> observableProduits = FXCollections.observableArrayList(produits);
        System.out.println(produits);
        produitTable.setItems(observableProduits);

        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_ProduitFini")).asString());
        qteColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());
    }
    public void retour(ActionEvent event) throws IOException {
        Node source = (Node) event.getSource();
        Stage oldStage = (Stage) source.getScene().getWindow();
        oldStage.close();
    }

    public void finaliserSaisie(ActionEvent e) throws SQLException {
        final String idCommande = id.getText();
        final String descriptionCommande = description.getText();
        final LocalDate dateCommande = date.getValue();
        System.out.println(dateCommande);
        dependencyManager.getCommandeRepository().create(idCommande, descriptionCommande, dateCommande);

        // Créer le tableau de bord en même temps
        // Chercher les produits associés à la commande
        // Pour chaque produit, faire la liste des matières premières et calculer le total (qte produit * nbr de matières premières)

        List<Map<String, Object>> listeBesoins = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> listeProduits = dependencyManager.getCommandeProduitFiniRepository().findByCommandeId(idCommande);
        for (Map<String, Object> produit : listeProduits){

            List<Map<String, Object>> listeMateriaux = dependencyManager.getProduitFiniMatierePremiereRepository().findByProduitId((String) produit.get("id_ProduitFini"));

            for(Map<String, Object> matiere : listeMateriaux){
                int qteTotale = (Integer)matiere.get("quantite")* (Integer) produit.get("quantite");
                Map<String, Object> temp = new HashMap<>();
                temp.put("reference", (String) matiere.get("matiere_id"));
                temp.put("quantite", qteTotale);
                int update = 0;

                // Vérifier si la ref est déjà dans la liste, si oui ajouter au nombre sinon ajouter la nouvelle matière première
                for(Map<String,Object> besoin : listeBesoins){
                    if(besoin.get("reference").equals(matiere.get("matiere_id"))){
                        int newQte = (Integer) besoin.get("quantite") + qteTotale;
                        besoin.put("quantite", newQte);
                        update = 1;
                    }
                }
                if(update == 0){
                    listeBesoins.add(temp);
                }
            }
            System.out.println(listeBesoins);
        }

        for(Map<String, Object> besoin : listeBesoins){
            dependencyManager.getCommandeMatierePremiereRepository().create(idCommande, (String) besoin.get("reference"), (Integer) besoin.get("quantite"));
        }

        dependencyManager.getConnection().commit();
        Node source = (Node) e.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

    }

    public void modifier(){

    }

    public void supprimerProduit(){
    dependencyManager.getCommandeProduitFiniRepository().delete(id.getText(), produitBox.getValue());
    }

    //Associer un produit fini à une commande
    public void ajouter(){
        //Vérifier que l'utilisateur a bien rentré un ID pour la commande
        String idCommande = id.getText();

        if(idCommande == null)  return;

        String idProduit = produitBox.getValue();
        String quantite = produitQuantite.getText();

        // Si le produit est deja associé à la commande alors on maj la quantité sinon on rajoute le lien
        if(dependencyManager.getCommandeProduitFiniRepository().findByIds(idCommande, idProduit) != null){
            dependencyManager.getCommandeProduitFiniRepository().updateQuantite(idCommande, idProduit, Integer.parseInt(quantite));
        } else {
            dependencyManager.getCommandeProduitFiniRepository().create(idCommande, idProduit, Integer.parseInt(quantite));
        }
        updateProduitTable();

    }

}
