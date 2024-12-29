package storageapp.model;

import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Map;

public interface ProduitFiniRepository {
    void create(String reference, String file);
    List<Map<String, Object>> findAll();
    void updateProduitId(String idProduitInit, String idProduitNouveau);
    Map<String,Object> findById(String idProduit);
    void getPicture(String id, String filename, ImageView imageView);
    void delete(String idProduit);


}
