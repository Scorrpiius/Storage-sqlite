package storageapp.model;

import java.time.LocalDate;
import java.util.Map;
import java.util.List;

public interface BonSortieRepository {
    void create(String id, LocalDate date);
    List<Map<String, Object>> findAll();
    Map<String, Object> findById(String id);
    void update(String idInitial, String idFinal, LocalDate nouvelleDate);


}
