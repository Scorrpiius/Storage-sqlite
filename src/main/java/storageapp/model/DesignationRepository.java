package storageapp.repository;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
public interface DesignationRepository {
    void create(String designation) throws SQLException;

    Map<String, Object> findById(String id);

    List<Map<String, Object>> findAll();

}
