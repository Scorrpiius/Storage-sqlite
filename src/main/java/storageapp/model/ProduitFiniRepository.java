package storageapp.model;

import java.util.List;
import java.util.Map;

public interface ProduitFiniRepository {
    void create(String reference, String file);
    List<Map<String, Object>> findAll();


}
