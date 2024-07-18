package stockapp.service;

import stockapp.model.BonSortie;
import stockapp.repository.*;

public class DependencyManager {
    private final FactureRepository factureRepository;
    private final EntreeRepository entreeRepository;
    private final SortieRepository sortieRepository;
    private final FicheStockRepository ficheStockRepository;
    private final BonSortieRepository bonSortieRepository;

    public DependencyManager() {

        factureRepository = new InMemoryFactureRepo();
        entreeRepository = new InMemoryEntreeRepo();
        sortieRepository = new InMemorySortieRepo();
        ficheStockRepository = new InMemoryFicheStockRepo();
        bonSortieRepository = new InMemoryBonSortieRepo();
    }


    public FactureRepository getFactureRepository() {
        return factureRepository;
    }

    public EntreeRepository getEntreeRepository() {
        return entreeRepository;
    }

    public SortieRepository getSortieRepository() {
        return sortieRepository;
    }

    public FicheStockRepository getFicheStockRepository() {
        return ficheStockRepository;
    }

    public BonSortieRepository getBonSortieRepository() {
        return bonSortieRepository;
    }
}
