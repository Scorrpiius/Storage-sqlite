package storageapp.model;

import java.util.List;
import java.util.Map;

public interface UniteMesureRepository {
    void create(String uniteMesure);
    List<Map<String, Object>> findAll();
    Map<String, Object> findById(String uniteMesure);
    void update(String uniteMesureInit, String newUniteMesure);

}
