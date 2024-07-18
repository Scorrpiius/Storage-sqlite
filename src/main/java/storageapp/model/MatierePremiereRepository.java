package storageapp.model;

import javafx.scene.image.ImageView;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface EntreeRepository {
    void create(String idRef, String idFacture, int quantite, double px_unitaire, Double px_revient, String categorie, String designation, String uniteMesure, String file) throws SQLException;

    Map<String, Object> findById(String id);

    List<Map<String, Object>> findByFactureId(String id);

    List<Map<String, Object>> findAll();

    List<Map<String, Object>> getAllInfos(String matierePremiereId);

    Map<String, Object> update(String refInit, String reference, String categorie, String designation, String idFacture, String tx_reel, String devise);
    void update(String id, String txReel, String valueDevise);

    void getPicture(String id, String filename, ImageView imageView);
    /*void delete(Entree entree);*/
}
