package storageapp.model;

import java.util.List;
import java.util.Map;

public interface SortieRepository {
    void create(int id, String id_bonSortie, String id_matierePremiere, String description, int quantite);
    List<Map<String, Object>> findAll();
    Map<String, Object> findByIds(String idMatPrem, String id_bonSortie);
    List<Map<String, Object>> getAllSortiesByBon(String id_bonSortie);
    void update(int id, String idBon, int quantite, String reference, String description);
    void updateBonId(String idBonInit, String idBonNouveau);
    void delete(String idMatPremiere, String idBonSortie);
}
