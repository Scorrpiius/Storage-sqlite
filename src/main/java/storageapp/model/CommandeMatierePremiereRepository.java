package storageapp.model;

import java.util.List;
import java.util.Map;

public interface CommandeMatierePremiereRepository {
    void create(String idCommande, String idMatierePremiere, int quantite);
    List<Map<String, Object>> findAll();
    List<Map<String, Object>> findByCommandeId(String idCommande);
    List<Map<String, Object>> findByMatiereId(String idMatierePremiere);
    Map<String, Object> findByIds(String idCommande, String idMatierePremiere);
    void updateQuantite(String idCommande, String idMatierePremiere, int quantite);
    void delete(String idCommande, String idMatierePremiere);
    void updateCommandeId(String idCommandeInit, String idCommandeNouveau);
}
