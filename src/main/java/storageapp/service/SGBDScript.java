package storageapp.service;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SGBDScript {
    public File database;
    public Stage stage;
   public SGBDScript(Stage s){
       stage = s;
   }

    public void createDB() throws IOException {
       //Choose directory to save DB, create file in directory, write all instructions to create the tables
        //Select directory
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);

        database = new File(selectedDirectory.getAbsoluteFile() + "/stockage.db");
        String script = """
                CREATE TABLE Categorie ( nom varchar(255) primary key);
                CREATE TABLE Designation ( nom varchar(255) primary key);
                CREATE TABLE Fournisseur ( nom varchar(255) primary key);
                CREATE TABLE Devise(nom varchar(255) primary key);
                CREATE TABLE Facture(id varchar(500) primary key, fournisseur varchar(255), date date, valeur_devise double, nom_devise varchar(255), tx_reel double, tx_theo double, nbr_matPrem int, foreign key (nom_devise) references Devise(nom));
                CREATE TABLE UniteMesure(nom varchar(255) primary key);
                CREATE TABLE FicheStock(id_matierePremiere varchar(500) primary key, categorie varchar(255), designation varchar(255), quantite int, uniteMesure varchar(255), foreign key (uniteMesure) references UniteMesure(nom), foreign key (id_matierePremiere) references MatierePremiere(id_reference), foreign key (categorie) references Categorie(categorie), foreign key (designation) references Designation(designation));
                CREATE TABLE MatierePremiere ( id_reference varchar(500), id_facture varchar(500), quantite int, px_unitaire decimal(10,2), px_revient_devise decimal(10,2), px_revient_local decimal(10,2), categorie varchar(255), designation varchar(255), uniteMesure varchar(255), img blob, primary key (id_reference, id_facture), foreign key (id_facture) references Facture(id), foreign key (categorie) references Categorie(categorie), foreign key (designation) references Designation(designation), foreign key (uniteMesure) references UniteMesure(nom));
                CREATE TABLE ProduitFini( reference varchar(500) primary key, image blob);
                CREATE TABLE ProduitFini_MatierePremiere (produit_reference varchar(500), matiere_id varchar(500), quantite integer, foreign key (produit_reference) references ProduitFini(reference), foreign key (matiere_id) references MatierePremiere(id_reference), primary key (produit_reference, matiere_id));
                CREATE TABLE Commande(id varchar(255) primary key, date date, description varchar(500));
                CREATE TABLE Commande_ProduitFini(id_Commande varchar(255), id_ProduitFini varchar(255), quantite integer, primary key (id_commande, id_ProduitFini), foreign key (id_ProduitFini) references ProduitFini(reference));
                CREATE TABLE Commande_MatierePremiere(id_Commande varchar(255), id_MatierePremiere varchar(255), quantite integer, primary key (id_Commande, id_MatierePremiere), foreign key (id_MatierePremiere) references MatierePremiere(id_reference));
                CREATE TABLE BonSortie(id varchar(255) primary key, date date);
                CREATE TABLE Sortie(id int, quantite int, id_bonSortie varchar(255), id_matierePremiere varchar(500), description varchar(500), primary key(id, id_bonSortie), foreign key (id_matierePremiere) references MatierePremiere(id_reference), foreign key (id_bonSortie) references BonSortie(id));""";

        FileWriter myWriter = new FileWriter(database.getAbsolutePath());
        myWriter.write(script);
        myWriter.close();

   }

    public File getDB(){
       return database;
    }
}
