package storageapp.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;
import java.io.IOException;
import java.util.Map;

public class mainController {
    @FXML
    private AnchorPane anchorPane, centralPane;

    private final DependencyManager dependencyManager;

    public mainController(DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
    }
    public void initialize() throws IOException, InterruptedException {
    }


    @FXML
    protected void showFacture(ActionEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilFacture.fxml"));
        fxmlLoader.setController(new accueilFacture(dependencyManager, centralPane));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(centralPane.getWidth(), centralPane.getHeight());

        centralPane.getChildren().setAll(newLoadedPane);
    }

    @FXML
    protected void showFiches(ActionEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilFiche.fxml"));
        fxmlLoader.setController(new accueilFiche(dependencyManager, centralPane));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(centralPane.getWidth(), centralPane.getHeight());

        centralPane.getChildren().setAll(newLoadedPane);
    }

    @FXML
    protected void showCommandes(ActionEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilCommande.fxml"));
        fxmlLoader.setController(new accueilCommande(dependencyManager, centralPane));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(centralPane.getWidth(), centralPane.getHeight());

        centralPane.getChildren().setAll(newLoadedPane);
    }

    @FXML
    protected void showProduits(ActionEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilProduit.fxml"));
        fxmlLoader.setController(new accueilProduit(dependencyManager, centralPane));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(centralPane.getWidth(), centralPane.getHeight());

        centralPane.getChildren().setAll(newLoadedPane);
    }

    @FXML
    protected void showBons(ActionEvent ignoredevent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilBon.fxml"));
        fxmlLoader.setController(new accueilBon(dependencyManager, centralPane));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(centralPane.getWidth(), centralPane.getHeight());
        centralPane.getChildren().setAll(newLoadedPane);
    }

}

