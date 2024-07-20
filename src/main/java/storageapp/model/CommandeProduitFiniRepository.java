package storageapp.model;

import java.util.List;
import java.util.Map;

public interface CommandeProduitFiniRepository {
    void create(String idCommande, String idProduit, int quantite);
    List<Map<String, Object>> findAll();
    List<Map<String, Object>> findByCommandeId(String idCommande);
    List<Map<String, Object>> findByProduitId(String idProduit);
    Map<String, Object> findByIds(String idCommande, String idProduit);
    void updateQuantite(String idCommande, String idProduit, int quantite);

}
