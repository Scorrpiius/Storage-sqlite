package storageapp.model;

import net.bytebuddy.asm.Advice;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface CommandeRepository {
    void create (String id, String description, LocalDate date);
    List<Map<String, Object>> findAll();
    Map<String, Object> findById(String id);
    void updateCommandeId(String idCommandeInit, String idCommandeNouveau);
    void updateInfos(String idCommande, String description, LocalDate date);
}
