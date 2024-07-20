package storageapp.model;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FactureRepository {

    void create(String id, LocalDate date, String supplier, double tauxTheo, double tauxReel, double valeur_devise, String nom_devise,  int nbrMatPrem) throws SQLException;
    Map<String, Object> findById(String id);
    List<Map<String, Object>> findAll();
    void update(String idInit,  String idNouveau, LocalDate date, String supplier, double tauxTheo, double tauxReel, double valeur_devise, String nom_devise,  int nbrMatPrem);
}
