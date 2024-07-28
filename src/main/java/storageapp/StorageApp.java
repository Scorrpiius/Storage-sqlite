package storageapp;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import storageapp.controller.mainController;
import storageapp.service.DependencyManager;


import java.io.*;
import java.net.URISyntaxException;
import java.sql.*;
import java.time.LocalDate;

public class StorageApp extends Application {
    @Override
    public void start(Stage stage) throws IOException, SQLException, URISyntaxException {
        DependencyManager dependencyManager = new DependencyManager();
        //Data alimentation
        //generateSampleData(dependencyManager);
        FXMLLoader fxmlLoader = new FXMLLoader(StorageApp.class.getResource("main.fxml"));
        mainController controller = new mainController(dependencyManager);
        fxmlLoader.setController(controller);

        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        stage.setTitle("Stockapp");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }

    public void generateSampleData(DependencyManager dependencyManager) throws SQLException {
        String data =  "E:\\bird-thumbnail.jpg";


        /* ----------- Factures ----------- */
        dependencyManager.getFactureRepository().create("A/24-00000125", LocalDate.parse("2024-03-07"),"Cano", 0.25, 0.24, 10.17,"Euro" , 8);
        dependencyManager.getFactureRepository().create("24MRF0142", LocalDate.parse("2024-03-14"), "Ming Tong Gold-Filled Zipper", 0.25,0.24,10.17, "Dollar",32);
        dependencyManager.getFactureRepository().create("MT240402-1 BA", LocalDate.parse("2024-04-02"), "MING TONG GOLD-FILLED ZIPPER", 0.25,0.24,10.17, "Euro", 6);
        dependencyManager.getFactureRepository().create("20109", LocalDate.parse("2024-03-19"), "Eurmoda", 0.25,0.24,10.17, "Euro",6);


        /* ----------- Devise ---------- */
        //dependencyManager.getDeviseRepository().create("Euro");
        dependencyManager.getDeviseRepository().create("Dollar");

        /* ----------- Fournisseur ----------- */
        dependencyManager.getFournisseurRepository().create("Cano");
        dependencyManager.getFournisseurRepository().create("Eurmoda");
        dependencyManager.getFournisseurRepository().create("Ming Tong Gold-Filled Zipper");
        dependencyManager.getFournisseurRepository().create("MING TONG GOLD-FILLED ZIPPER");

        /* ----------- MatierePremiere ----------- */
        dependencyManager.getEntreeRepository().create("MTL.SALPA0.4MM.REF.SNATURAL", "A/24-00000125", 200, 1.64,2.03,20.6 ,"cat_1", "des_1", "um_1", data);
        dependencyManager.getEntreeRepository().create("MTL. SALPA 0.8MM REF.S NATURAL", "A/24-00000125", 200, 4.580,5.67,11.6 ,"cat_2", "des_2", "um_1", data);
        dependencyManager.getEntreeRepository().create("MTL. TEXPUN BLANCO-12 40 GR. AUTOADHESIVO", "A/24-00000125", 200, 1.820,2.25,23.14 ,"cat_1", "des_3", "um_1", data);
        dependencyManager.getEntreeRepository().create("MILL. PAPEL SULFITO 42X75 BLANCO", "A/24-00000125", 200, 13.390,16.6,176.71 ,"cat_1", "des_4", "um_1", data);
        dependencyManager.getEntreeRepository().create("MTS. FEUTRE/8 C/ 0091", "A/24-00000125", 200, 0.476,0.59,6.05 ,"cat_3", "des_5", "um_1", data);
        dependencyManager.getEntreeRepository().create("MTS. FEUTRE/8 C/ 0130", "A/24-00000125", 200, 0.837,1.03,10.64 ,"cat_3", "des_6", "um_1", data);
        dependencyManager.getEntreeRepository().create("MTS. FEUTRE/8 C/ 0118", "A/24-00000125", 200, 0.476,0.59,6.05 ,"cat_4", "des_7", "um_1", data);
        dependencyManager.getEntreeRepository().create("MTS. FEUTRE/8 C/ 0064", "A/24-00000125", 200, 0.522,0.64,6.63 ,"cat_4", "des_8", "um_1", data);

        dependencyManager.getEntreeRepository().create("RJ228", "MT240402-1 BA", 1234, 0.420,0.52,5.33 , "cat_1", "Adjuster 31 mm", "pcs", data);
        dependencyManager.getEntreeRepository().create("RJ226", "MT240402-1 BA", 1135, 0.460,0.57,5.84 , "cat_1", "Adjuster 50 mm", "pcs", data);
        dependencyManager.getEntreeRepository().create("RJ229", "MT240402-1 BA", 1800, 0.220,0.27,2.79 , "cat_1", "D ring 10*2mm", "pcs", data);
        dependencyManager.getEntreeRepository().create("RJ230", "MT240402-1 BA", 1747, 0.250,0.31,3.17 , "cat_1", "D ring 15*4mm", "pcs", data);
        dependencyManager.getEntreeRepository().create("RJ227", "MT240402-1 BA", 2000, 0.400,0.496,5.08 , "cat_1", "D ring 27*4.2mm", "pcs", data);
        dependencyManager.getEntreeRepository().create("17", "MT240402-1 BA", 2681, 0.130,0.16,1.65 , "cat_1", "Iron square ring", "pcs", data);

        dependencyManager.getEntreeRepository().create("Z3-MS-BJ D580,15cm", "24MRF0142", 100, 0.643,0.80,8.17 , "cat_1", "Polished double teeth zipper in nickel", "pcs", data);
        dependencyManager.getEntreeRepository().create("Z3-MS-BJ D580,20cm", "24MRF0142", 150, 0.723,0.90,9.19 , "cat_1", "Polished double teeth zipper in nickel", "pcs", data);
        dependencyManager.getEntreeRepository().create("Z3-MS-BJ D580,28cm", "24MRF0142", 100, 0.851,1.05,10.81 , "cat_1", "Polished double teeth zipper in nickel", "pcs", data);
        dependencyManager.getEntreeRepository().create("Z3-MS-BJ D580,35cm", "24MRF0142", 200, 0.963,1.19,12.24 , "cat_1", "Polished double teeth zipper in nickel", "pcs", data);
        dependencyManager.getEntreeRepository().create("Z5-MS-BJ D188,20cm", "24MRF0142", 30, 0.793,0.98,10.08 , "cat_1", "Polished double teeth zipper in nickel", "pcs", data);
        dependencyManager.getEntreeRepository().create("Z5-MS-BJ D030,31cm", "24MRF0142", 20, 0.969,1.23,12.32 , "cat_1", "Polished double teeth zipper in nickel", "pcs", data);
        dependencyManager.getEntreeRepository().create("Z5-MS-BJ D580,20cm", "24MRF0142", 700, 0.793,0.98,10.08 , "cat_1", "Polished double teeth zipper in nickel", "pcs", data);
        dependencyManager.getEntreeRepository().create("Z5-MS-BJ D316,22cm", "24MRF0142", 20, 0.825,1.02,10.49 , "cat_1", "Polished double teeth zipper in nickel", "pcs", data);
        dependencyManager.getEntreeRepository().create("Z5-MS-BJ D580,22cm", "24MRF0142", 20, 0.825,1.02,10.49 , "cat_1", "Polished double teeth zipper in nickel", "pcs", data);
        dependencyManager.getEntreeRepository().create("Z5-MS-BJ D580,28cm", "24MRF0142", 30, 0.921,1.14,11.7 , "cat_1", "Polished double teeth zipper in nickel", "pcs", data);
        dependencyManager.getEntreeRepository().create("Z5-MS-BJ D236,28cm", "24MRF0142", 20, 0.921,1.14,11.7 , "cat_1", "Polished double teeth zipper in nickel", "pcs", data);
        dependencyManager.getEntreeRepository().create("Z5-MS-BJ D563,28cm", "24MRF0142", 20, 0.921,1.14,11.7 , "cat_1", "Polished double teeth zipper in nickel", "pcs", data);
        dependencyManager.getEntreeRepository().create("Z5-MS-BJ D580,31cm", "24MRF0142", 120, 0.969,1.23,12.32 , "cat_1", "Polished double teeth zipper in nickel", "pcs", data);
        dependencyManager.getEntreeRepository().create("Z5-MS-BJ D563,34cm", "24MRF0142", 40, 1.157,1.43,14.7 , "cat_1", "Polished double teeth zipper in nickel", "pcs", data);
        dependencyManager.getEntreeRepository().create("Z5-MS-BJ D826,22cm", "24MRF0142", 20, 0.825,1.02,10.49 , "cat_1", "Polished double teeth zipper in nickel", "pcs", data);
        dependencyManager.getEntreeRepository().create("Z5-MS-BJ D826,28.5cm", "24MRF0142", 20, 0.929,1.15,11.8 , "cat_1", "Polished double teeth zipper in nickel", "pcs", data);
        dependencyManager.getEntreeRepository().create("Y3999-Adjuster", "24MRF0142", 1661, 0.320,0.40, 4.06 , "cat_1", "Adjuster", "pcs", data);
        dependencyManager.getEntreeRepository().create("15-Iron D ring", "24MRF0142", 1724, 0.110,0.14,1.40 , "cat_1", "Iron D ring", "pcs", data);
        dependencyManager.getEntreeRepository().create("16-Iron D ring", "24MRF0142", 6259, 0.120,0.15,2.54 , "cat_1", "Iron D ring", "pcs", data);
        dependencyManager.getEntreeRepository().create("18-Iron oval ring", "24MRF0142", 1057, 0.250,0.31,3.17 , "cat_1", "Iron oval ring", "pcs", data);
        dependencyManager.getEntreeRepository().create("19-Iron oval ring", "24MRF0142", 1217, 0.380,0.47,4.83 , "cat_1", "Iron oval ring", "pcs", data);

        dependencyManager.getEntreeRepository().create("F0120/001/10/12", "20109", 302, 0.360,0.45,4.57 , "accessoire", "rings nickel", "nr", data);
        dependencyManager.getEntreeRepository().create("M0457/001/10/06", "20109", 2, 42.0,52.08,532.35 , "accessoire", "rivet nickel", "ml", data);
        dependencyManager.getEntreeRepository().create("X1246/150/00/20", "20109", 2, 13.0,16.12,165.26 , "accessoire", "rivetti 036 testa", "ml", data);
        dependencyManager.getEntreeRepository().create("D0259/000/10/04", "20109", 100, 1.7,2.108,21.6 , "accessoire", "lock nickel", "nr", data);
        dependencyManager.getEntreeRepository().create("D0873/032/10/41", "20109", 200, 0.650,0.806,8.26 , "accessoire", "rings nickel", "nr", data);
        dependencyManager.getEntreeRepository().create("D1154/015/10/41", "20109", 300, 0.450,0.56,5.72 , "accessoire", "rings nickel", "nr", data);

        /* -------- Fiche de stock -------- */
        dependencyManager.getFicheStockRepository().create("MTL.SALPA0.4MM.REF.SNATURAL", "cat_1", "des_1",200, "pcs" );
        dependencyManager.getFicheStockRepository().create("MTL. SALPA 0.8MM REF.S NATURAL", "cat_2", "des_2",200, "pcs");
        dependencyManager.getFicheStockRepository().create("MTL. TEXPUN BLANCO-12 40 GR. AUTOADHESIVO", "cat_1", "des_3",200, "pcs");
        dependencyManager.getFicheStockRepository().create("MILL. PAPEL SULFITO 42X75 BLANCO", "cat_1", "des_4",200,"pcs");
        dependencyManager.getFicheStockRepository().create("MTS. FEUTRE/8 C/ 0091", "cat_3", "des_5",200, "pcs" );
        dependencyManager.getFicheStockRepository().create("MTS. FEUTRE/8 C/ 0130", "cat_3", "des_6",200,"pcs");
        dependencyManager.getFicheStockRepository().create("MTS. FEUTRE/8 C/ 0118", "cat_4", "des_7",200,"pcs");
        dependencyManager.getFicheStockRepository().create("MTS. FEUTRE/8 C/ 0064", "cat_4", "des_8",200,"pcs");

        dependencyManager.getFicheStockRepository().create("RJ228", "cat_1", "Adjuster 31 mm",1234,"pcs");
        dependencyManager.getFicheStockRepository().create("RJ226", "cat_1", "Adjuster 50 mm",1135, "pcs");
        dependencyManager.getFicheStockRepository().create("RJ229", "cat_1", "D ring 10*2mm",1800, "pcs");
        dependencyManager.getFicheStockRepository().create("RJ230", "cat_1", "D ring 15*4mm",1747,"pcs");
        dependencyManager.getFicheStockRepository().create("RJ227", "cat_1", "D ring 27*4.2mm",2000, "pcs");
        dependencyManager.getFicheStockRepository().create("17", "cat_1", "Iron square ring",2681, "pcs");

        dependencyManager.getFicheStockRepository().create("Z3-MS-BJ D580,15cm", "cat_1", "Polished double teeth zipper in nickel",100 , "pcs");
        dependencyManager.getFicheStockRepository().create("Z3-MS-BJ D580,20cm", "cat_1", "Polished double teeth zipper in nickel",150,"pcs");
        dependencyManager.getFicheStockRepository().create("Z3-MS-BJ D580,28cm", "cat_1", "Polished double teeth zipper in nickel",100, "pcs");
        dependencyManager.getFicheStockRepository().create("Z3-MS-BJ D580,35cm", "cat_1", "Polished double teeth zipper in nickel",200, "pcs");
        dependencyManager.getFicheStockRepository().create("Z5-MS-BJ D188,20cm", "cat_1", "Polished double teeth zipper in nickel",30,"pcs");
        dependencyManager.getFicheStockRepository().create("Z5-MS-BJ D030,31cm", "cat_1", "Polished double teeth zipper in nickel",20 ,"pcs");
        dependencyManager.getFicheStockRepository().create("Z5-MS-BJ D580,20cm", "cat_1", "Polished double teeth zipper in nickel",700, "pcs");
        dependencyManager.getFicheStockRepository().create("Z5-MS-BJ D316,22cm", "cat_1", "Polished double teeth zipper in nickel",20, "pcs");
        dependencyManager.getFicheStockRepository().create("Z5-MS-BJ D580,22cm", "cat_1", "Polished double teeth zipper in nickel",20, "pcs");
        dependencyManager.getFicheStockRepository().create("Z5-MS-BJ D580,28cm", "cat_1", "Polished double teeth zipper in nickel",30,"pcs");
        dependencyManager.getFicheStockRepository().create("Z5-MS-BJ D236,28cm", "cat_1", "Polished double teeth zipper in nickel",20,"pcs");
        dependencyManager.getFicheStockRepository().create("Z5-MS-BJ D563,28cm", "cat_1", "Polished double teeth zipper in nickel",20,"pcs");
        dependencyManager.getFicheStockRepository().create("Z5-MS-BJ D580,31cm", "cat_1", "Polished double teeth zipper in nickel",120,"pcs");
        dependencyManager.getFicheStockRepository().create("Z5-MS-BJ D563,34cm", "cat_1", "Polished double teeth zipper in nickel",40, "pcs");
        dependencyManager.getFicheStockRepository().create("Z5-MS-BJ D826,22cm", "cat_1", "Polished double teeth zipper in nickel",20, "pcs");
        dependencyManager.getFicheStockRepository().create("Z5-MS-BJ D826,28.5cm", "cat_1", "Polished double teeth zipper in nickel",20 ,"pcs");
        dependencyManager.getFicheStockRepository().create("Y3999-Adjuster", "cat_1", "Adjuster",1661, "pcs");
        dependencyManager.getFicheStockRepository().create("15-Iron D ring", "cat_1", "Iron D ring",1724, "pcs");
        dependencyManager.getFicheStockRepository().create("16-Iron D ring", "cat_1", "Iron D ring",6259, "pcs");
        dependencyManager.getFicheStockRepository().create("18-Iron oval ring", "cat_1", "Iron oval ring",1057, "pcs");
        dependencyManager.getFicheStockRepository().create("19-Iron oval ring", "cat_1", "Iron oval ring",1217,"pcs");

        dependencyManager.getFicheStockRepository().create("F0120/001/10/12", "accessoire", "rings nickel", 302, "pcs");
        dependencyManager.getFicheStockRepository().create("M0457/001/10/06", "accessoire", "rivet nickel", 2 ,"pcs");
        dependencyManager.getFicheStockRepository().create("X1246/150/00/20", "accessoire", "rivetti 036 testa", 2 ,"pcs");
        dependencyManager.getFicheStockRepository().create("D0259/000/10/04", "accessoire", "lock nickel", 2,"pcs");
        dependencyManager.getFicheStockRepository().create("D0873/032/10/41", "accessoire", "rings nickel", 200, "pcs");
        dependencyManager.getFicheStockRepository().create("D1154/015/10/41",  "accessoire", "rings nickel", 300, "pcs");

        dependencyManager.getCategorieRepository().create("accessoire");
        dependencyManager.getCategorieRepository().create("cat_1");
        dependencyManager.getCategorieRepository().create("cat_2");
        dependencyManager.getCategorieRepository().create("cat_3");
        dependencyManager.getCategorieRepository().create("cat_4");

        dependencyManager.getDesignationRepository().create("rings nickel");
        dependencyManager.getDesignationRepository().create("rivet nickel");
        dependencyManager.getDesignationRepository().create("rivetti 036 testa");
        dependencyManager.getDesignationRepository().create("lock nickel");
        dependencyManager.getDesignationRepository().create("Adjuster");
        dependencyManager.getDesignationRepository().create("Iron D ring");
        dependencyManager.getDesignationRepository().create("Iron oval ring");
        dependencyManager.getDesignationRepository().create("Polished double teeth zipper in nickel");
        dependencyManager.getDesignationRepository().create("Adjuster 31 mm");
        dependencyManager.getDesignationRepository().create("Adjuster 50 mm");
        dependencyManager.getDesignationRepository().create("D ring 10*2mm");
        dependencyManager.getDesignationRepository().create("D ring 15*4mm");
        dependencyManager.getDesignationRepository().create("D ring 27*4.2mm");
        dependencyManager.getDesignationRepository().create( "Iron square ring");
        dependencyManager.getDesignationRepository().create("des_1");
        dependencyManager.getDesignationRepository().create("des_2");
        dependencyManager.getDesignationRepository().create("des_3");
        dependencyManager.getDesignationRepository().create("des_4");
        dependencyManager.getDesignationRepository().create("des_5");
        dependencyManager.getDesignationRepository().create("des_6");
        dependencyManager.getDesignationRepository().create("des_7");
        dependencyManager.getDesignationRepository().create("des_8");

        //dependencyManager.getUniteMesureRepository().create("pcs");

        dependencyManager.getConnection().commit();
    }

    public static void main(String[] args) {
        launch();
    }
}
