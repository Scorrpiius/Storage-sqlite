package stockapp.repository;

import stockapp.model.Categorie;
import stockapp.model.Designation;
import stockapp.model.FicheStock;
import stockapp.model.MouvementProduit;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class InMemoryFicheStockRepo implements FicheStockRepository {
    final private HashMap<String, FicheStock> hashMapFicheStock;

    public InMemoryFicheStockRepo(){
        hashMapFicheStock = new HashMap<>();
    }

    @Override
    public FicheStock create(Categorie categorie, String reference, Designation designation, String unite_mesure, String lien_image, double quantite_tot, List<MouvementProduit> historique) {
        return new FicheStock(categorie, reference, designation, unite_mesure, lien_image, quantite_tot, historique);
    }

    @Override
    public List<FicheStock> findAll() {
        return null;
    }

    @Override
    public FicheStock findById(String id) {
        return hashMapFicheStock.get(id);
    }

    @Override
    public FicheStock update(FicheStock ficheStock) {
        hashMapFicheStock.put(ficheStock.getReference(), ficheStock);
        return hashMapFicheStock.get(ficheStock.getReference());
    }

    @Override
    public void delete(FicheStock ficheStock) {
        hashMapFicheStock.remove(ficheStock.getReference());
    }
}
