package storageapp.repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface FicheStockRepository {

    void create(String id, String categorie, String designation, int quantite) throws SQLException;

    Map<String, Object> findById(String id);

    List<Map<String, Object>> findAll();

    void update(String query) throws SQLException;

    List<Map<String, Object>> filter(String categorie, String designation, String reference);

    List<Map<String, Object>> getAllId();

    /*void delete(FicheStock ficheStock);*/
}
