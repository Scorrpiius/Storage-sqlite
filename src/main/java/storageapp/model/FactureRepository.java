package storageapp.repository;


import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FactureRepository {

    void create(String id, LocalDate date, String supplier, double tauxTheo, double tauxReel, double devise, int nbrMatPrem) throws SQLException;

    Map<String, Object> findById(String id);

    List<Map<String, Object>> findAll();

    /*Facture update(Facture facture);

    void delete(Facture facture);*/
}
