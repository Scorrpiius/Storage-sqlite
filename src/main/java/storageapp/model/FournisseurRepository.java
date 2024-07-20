package storageapp.model;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
public interface FournisseurRepository {
    void create(String fournisseur) throws SQLException;
    Map<String, Object> findById(String id);
    List<Map<String, Object>> findAll();

}
