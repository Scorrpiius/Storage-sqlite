package stockapp.repository;

import stockapp.model.Entree;
import stockapp.model.Facture;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class InMemoryFactureRepo implements FactureRepository{
    final private HashMap<Long, Facture> hashMapFacture;

    public InMemoryFactureRepo(){
        hashMapFacture = new HashMap<>();
    }

    @Override
    public Facture create(long id, LocalDate date, String supplier, double tauxTheo, double tauxReel, String devise, List<Entree> entrees) {
        Facture f = new Facture(id, date, supplier, tauxTheo, tauxReel, devise, entrees);
        hashMapFacture.put(f.getId(), f);
        return f;
    }

    @Override
    public Facture findById(long id) {
        return hashMapFacture.get(id);
    }

    @Override
    public List<Facture> findAll() {
        List<Facture> res = new ArrayList<>();
        for (java.util.Map.Entry<Long, Facture> mapEntry : hashMapFacture.entrySet()) {
            res.add(mapEntry.getValue());
        }

        return res;
    }

    @Override
    public Facture update(Facture facture) {
        hashMapFacture.put(facture.getId(), facture);
        return hashMapFacture.get(facture.getId());
    }

    @Override
    public void delete(Facture facture) {
        hashMapFacture.remove(facture.getId());
    }
}
