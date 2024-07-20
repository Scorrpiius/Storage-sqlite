package storageapp.model;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DeviseRepository {
    void create(String devise) throws SQLException;
    Map<String, Object> findById(String id);
    List<Map<String, Object>> findAll();

}
