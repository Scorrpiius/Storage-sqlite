package storageapp.model;

import javafx.scene.image.ImageView;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface MatierePremiereRepository {
    void create(String idRef, String idFacture, int quantite, double px_unitaire, double px_revient_devise, double px_revient_local, String categorie, String designation, String uniteMesure, String file) throws SQLException;
    Map<String, Object> findById(String id);
    Map<String, Object> findByIds(String factureId, String matiereId);
    List<Map<String, Object>> findByFactureId(String id);
    List<Map<String, Object>> findAll();
    List<Map<String, Object>> getAllInfos(String matierePremiereId);
    Map<String, Object> update(String factureId, String refInit, String reference, String categorie, String designation, String quantite, String tx_reel, String devise);
    void update(String idInit, String idNouveaux, double txReel, double valueDevise);
    void getPicture(String id, String filename, ImageView imageView);
    void delete(String idRef, String idFacture);
}
