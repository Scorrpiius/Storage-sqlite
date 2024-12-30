package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import storageapp.StorageApp;
import storageapp.service.DependencyManager;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ficheStockController {
    @FXML
    private AnchorPane root;
    @FXML
    private ImageView img;
    @FXML
    private Label titlePage;
    private final String ficheStockId;
    @FXML
    private TableView<Map<String, Object>> historiqueTable;
    private final DependencyManager dependencyManager;
    @FXML
    private TextField reference, categorie, designation, uniteMesure, quantite, pxUnitMoyen, pxTotal;
    @FXML
    private TableColumn<Map<String, Object>, String> idColumn, dateColumn, pxUnitColumn, pxRevientDeviseColumn, pxRevientLocalColumn, qteColumn;

    public ficheStockController(DependencyManager dependencyManager, String ficheStockId, AnchorPane root){
        this.dependencyManager = dependencyManager;
        this.ficheStockId = ficheStockId;
        this.root = root;
    }
    public void initialize(){
        titlePage.setText("Fiche de stock - Produit " + ficheStockId);
        updateHistorique();
        updateAllData();
        updatePrix();
        telechargerImage();
        historiqueTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }
    @FXML
    public void telechargerImage(){
        dependencyManager.getEntreeRepository().getPicture(ficheStockId, null, img);
    }
    @FXML
    public void updatePrix(){
        List<Map<String,Object>> data = dependencyManager.getEntreeRepository().getAllInfos(ficheStockId);
        double prixUnitMoyen = 0.0;
        double prixTotal = 0.0;
        for (Map<String, Object> d : data ){
            prixUnitMoyen += Double.parseDouble(String.valueOf(d.get("px_unitaire")));
            prixTotal += Double.parseDouble(String.valueOf(d.get("px_unitaire"))) * Double.parseDouble(String.valueOf(d.get("quantite")));
        }
        prixUnitMoyen /= (long) data.size();
        this.pxUnitMoyen.setText(String.valueOf(prixUnitMoyen));
        this.pxTotal.setText(String.valueOf(prixTotal));

        this.pxTotal.setEditable(false);
        this.pxUnitMoyen.setEditable(false);
    }
    public void updateHistorique(){
        List<Map<String,Object>> historiqueData = dependencyManager.getEntreeRepository().getAllInfos(ficheStockId);
        if (!historiqueTable.getItems().isEmpty()) {
            historiqueTable.getItems().clear();
        }

        ObservableList<Map<String, Object>> observableHistorique = FXCollections.observableArrayList(historiqueData);
        historiqueTable.setItems(observableHistorique);

        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_facture")).asString());
        dateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("date")).asString());
        pxUnitColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("px_unitaire")).asString());
        qteColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());
        pxRevientDeviseColumn.setCellValueFactory(cellData -> {
            Object objet = cellData.getValue().get("px_revient_devise");
            return new SimpleObjectProperty<>(String.format("%.2f", objet));
        });
        pxRevientLocalColumn.setCellValueFactory(cellData -> {
            Object objet = cellData.getValue().get("px_revient_local");
            return new SimpleObjectProperty<>(String.format("%.2f", objet));
        });
    }
    public void updateAllData(){
        Map<String, Object> ficheStock = dependencyManager.getFicheStockRepository().findById(ficheStockId);
        String categorie = (String) ficheStock.get("categorie");
        String designation = (String) ficheStock.get("designation");
        String uniteMesure = (String) ficheStock.get("uniteMesure");
        int quantite = (int) ficheStock.get("quantite");

        this.categorie.setText(categorie);
        this.designation.setText(designation);
        this.quantite.setText(String.valueOf(quantite));
        this.reference.setText(ficheStockId);
        this.uniteMesure.setText(uniteMesure);

        this.categorie.setEditable(false);
        this.designation.setEditable(false);
        this.quantite.setEditable(false);
        this.reference.setEditable(false);
        this.uniteMesure.setEditable(false);
    }

    public void imprimer(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage primaryStage = (Stage) source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                saveTableDataAsPDF(historiqueTable.getItems(), file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void saveTableDataAsPDF(ObservableList<Map<String, Object>> dataList, File file) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);


            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                PDTrueTypeFont font = PDTrueTypeFont.load(document, PDDocument.class.getResourceAsStream(
                        "/org/apache/pdfbox/resources/ttf/LiberationSans-Regular.ttf"), WinAnsiEncoding.INSTANCE);
                contentStream.setFont(font, 12);
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(25, 750);
                contentStream.showText("Fiche de stock - " + ficheStockId);
                contentStream.newLine();
                contentStream.showText("Référence : " + ficheStockId);
                contentStream.newLine();
                contentStream.showText("Catégorie : " + categorie.getText());
                contentStream.newLine();
                contentStream.showText("Désignation : " + designation.getText());
                contentStream.newLine();
                contentStream.showText("Quantité totale : " + quantite.getText() + " " + uniteMesure.getText());
                contentStream.newLine();
                contentStream.showText("Historique d'entrée : ");
                contentStream.newLine();

                for (Map<String, Object> data : dataList) {
                    contentStream.showText("Facture : " + data.get("id_facture") + "  Date : " + data.get("date") + " Quantité : " + data.get("quantite") + " Prix unitaire : " + data.get("px_unitaire") );
                    contentStream.newLine();
                }
                contentStream.showText("Prix unitaire moyen : " + pxUnitMoyen.getText());
                contentStream.newLine();
                contentStream.showText("Prix total : " + pxTotal.getText());

                contentStream.endText();
            }

            document.save(file);
        }
    }

    public void retourAccueil(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilFiche.fxml"));
        fxmlLoader.setController(new accueilFiche(dependencyManager, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);
    }

}
