package storageapp.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class bonSortieController {
    @FXML
    private AnchorPane root;
    @FXML
    private Label titleLabel;
    private final String bonSortieId;
    @FXML
    private TextField idBonField, dateBonField;
    private final DependencyManager dependencyManager;
    @FXML
    private TableView<Map<String, Object>> sortiesTable;
    @FXML
    private TableColumn<Map<String, Object>, String> idSortieCol, refSortieCol, qteSortieCol, descrSortieCol;

    public bonSortieController(DependencyManager dependencyManager, String bonSortieId, AnchorPane root) {
        this.dependencyManager = dependencyManager;
        this.bonSortieId = bonSortieId;
        this.root = root;
    }

    public void initialize(){
        titleLabel.setText("Bon de sortie N° " + bonSortieId);
        updateSortieTable();
        updateData();
        sortiesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    @FXML
    public void updateData(){
        Map<String, Object> bonSortie = dependencyManager.getBonSortieRepository().findById(bonSortieId);
        idBonField.setEditable(false);
        dateBonField.setEditable(false);

        idBonField.setText(String.valueOf(bonSortie.get("id")));
        dateBonField.setText((String) bonSortie.get("date"));
    }
    @FXML
    public void updateSortieTable(){
        if (!sortiesTable.getItems().isEmpty()) {
            sortiesTable.getItems().clear();
        }

        List<Map<String, Object>> sorties = dependencyManager.getSortieRepository().getAllSortiesByBon(String.valueOf(bonSortieId));
        ObservableList<Map<String, Object>> observableSorties = FXCollections.observableArrayList(sorties);
        sortiesTable.setItems(observableSorties);

        idSortieCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id")).asString());
        refSortieCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("id_matierePremiere")).asString());
        qteSortieCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("quantite")).asString());
        descrSortieCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get("description")).asString());
    }

    public void retourAccueil(ActionEvent event) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("accueilBon.fxml"));
        fxmlLoader.setController(new accueilBon(dependencyManager, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);
    }
    public void modifierBonSortie(ActionEvent event) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(StorageApp.class.getResource("modifierBon.fxml"));
        fxmlLoader.setController(new modifierBonController(dependencyManager, bonSortieId, root));
        AnchorPane newLoadedPane = fxmlLoader.load();
        newLoadedPane.setPrefSize(root.getWidth(), root.getHeight());
        root.getChildren().setAll(newLoadedPane);
    }
    protected void imprimer(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage primaryStage = (Stage) source.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le fichier PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try {
                saveTableDataAsPDF(sortiesTable.getItems(), file);
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
                contentStream.showText("Bon de sortie - " + bonSortieId);
                contentStream.newLine();
                contentStream.showText(dateBonField.getText());
                contentStream.newLine();

                for (Map<String, Object> data : dataList) {
                    contentStream.showText("ID : " + data.get("id") + "  Reference : " + data.get("id_matierePremiere") + " Quantité : " + data.get("quantite") + " Description : " + data.get("description") );
                    contentStream.newLine();
                }

                contentStream.endText();
            }

            document.save(file);
        }
    }


}
