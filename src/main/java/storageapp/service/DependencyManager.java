package storageapp.service;

import storageapp.StorageApp;
import storageapp.model.*;
import storageapp.repository.*;


import java.net.URISyntaxException;
import java.sql.*;


public class DependencyManager {
    private final FactureRepository factureRepository;
    private final MatierePremiereRepository entreeRepository;
    private final FicheStockRepository ficheStockRepository;
    private final CategorieRepository categorieRepository;
    private final DeviseRepository deviseRepository;
    private final FournisseurRepository fournisseurRepository;
    private final DesignationRepository designationRepository;
    private final SortieRepository sortieRepository;
    private final BonSortieRepository bonSortieRepository;
    private final UniteMesureRepository uniteMesureRepository;
    private final ProduitFiniRepository produitFiniRepository;
    private final ProduitFini_MatierePremiereRepository produitFiniMatierePremiereRepository;
    private final CommandeRepository commandeRepository;
    private final CommandeProduitFiniRepository commandeProduitFiniRepository;
    private final CommandeMatierePremiereRepository commandeMatierePremiereRepository;
    private final Connection connection;

    public DependencyManager() throws SQLException, URISyntaxException {
        /* Connect to the database */

        /* Créer la base de données */
        //String jdbcUrl = "jdbc:sqlite:/C:\\Users\\NEWBOSS\\OneDrive\\Documents\\Logiciel de stockage\\sqlite-tools-win-x64-3460000\\storage.db";
        System.out.println("Hello");
        String bd = String.valueOf(StorageApp.class.getResource("storage.db"));
        String modified_bd = bd.substring(4);
        System.out.println(modified_bd);


        connection = DriverManager.getConnection("jdbc:sqlite" + modified_bd);
        connection.setAutoCommit(false);

        factureRepository = new InMemoryFactureRepo(connection);
        entreeRepository = new InMemoryMatierePremiereRepo(connection);
        ficheStockRepository = new InMemoryFicheStockRepo(connection);
        categorieRepository = new InMemoryCategorieRepo(connection);
        designationRepository = new InMemoryDesignationRepo(connection);
        deviseRepository = new InMemoryDeviseRepo(connection);
        fournisseurRepository = new InMemoryFournisseurRepo(connection);
        sortieRepository = new InMemorySortieRepo(connection);
        bonSortieRepository = new InMemoryBonSortieRepo(connection);
        uniteMesureRepository = new InMemoryUniteMesureRepo(connection);
        produitFiniRepository = new InMemoryProduitFiniRepo(connection);
        produitFiniMatierePremiereRepository = new InMemoryProduitMatiereRepo(connection);
        commandeRepository = new InMemoryCommandeRepo(connection);
        commandeProduitFiniRepository = new InMemoryCommandeProduitRepo(connection);
        commandeMatierePremiereRepository = new InMemoryCommandeMatierePremiereRepo(connection);

    }

    public Connection getConnection(){
        return connection;
    }
    public FactureRepository getFactureRepository() {
        return factureRepository;
    }
    public MatierePremiereRepository getEntreeRepository() {
        return entreeRepository;
    }
    public FicheStockRepository getFicheStockRepository() { return ficheStockRepository; }
    public CategorieRepository getCategorieRepository() { return categorieRepository;}
    public DesignationRepository getDesignationRepository() { return designationRepository; }
    public DeviseRepository getDeviseRepository(){ return deviseRepository; }
    public FournisseurRepository getFournisseurRepository(){ return fournisseurRepository; }
    public SortieRepository getSortieRepository() { return sortieRepository; }
    public BonSortieRepository getBonSortieRepository() { return bonSortieRepository; }
    public UniteMesureRepository getUniteMesureRepository(){ return uniteMesureRepository;}
    public ProduitFiniRepository getProduitFiniRepository(){ return produitFiniRepository;}
    public ProduitFini_MatierePremiereRepository getProduitFiniMatierePremiereRepository(){ return produitFiniMatierePremiereRepository;}
    public CommandeRepository getCommandeRepository(){ return commandeRepository;}
    public CommandeProduitFiniRepository getCommandeProduitFiniRepository(){ return commandeProduitFiniRepository;}
    public CommandeMatierePremiereRepository getCommandeMatierePremiereRepository(){ return commandeMatierePremiereRepository;}
}
