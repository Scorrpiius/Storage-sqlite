package storageapp.model;

import java.util.List;
import java.util.Map;

public interface ProduitFini_MatierePremiereRepository {
    void create(String produitReference, String matiereReference, int quantite);
    List<Map<String, Object>> findByProduitId(String produitId);
    Map<String, Object> findByIds(String produitId, String matiereId);
    void updateQuantite(String produitId, String matiereId, int quantite);
    void update(String produitId, String matiereInit, String newMatiere, String quantite);
    void delete(String produitId, String matiereId);
    void updateProduitId(String idProduitInit, String idProduitNouveau);
    void deleteAll(String produitId);
}
