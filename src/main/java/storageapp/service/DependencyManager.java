package storageapp.service;

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
    private final Connection connection;

    public DependencyManager(String dbPath) throws SQLException, URISyntaxException {
        /* Connect to the database */

        connection = DriverManager.getConnection("jdbc:sqlite:E:\\SQLite\\sqlite-tools-win-x64-3450200\\storage.db");
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
}
