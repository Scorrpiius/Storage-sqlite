
package storageapp.repository;

import storageapp.model.EntreeRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryEntreeRepo implements EntreeRepository {
    private Connection connection;

    public InMemoryEntreeRepo(Connection connection){
        this.connection = connection;
    }

    @Override
    public void create(String idRef, String idFacture, int quantite, double px_unitaire, double px_revient, String categorie, String designation, String uniteMesure) throws SQLException{

        String sql = "insert into MatierePremiere values ('" + idRef + "','" + idFacture + "'," + quantite + "," + px_unitaire + "," + px_revient + ",'" + categorie +"','" + designation +"', '" + uniteMesure + "');";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Execute the update (since it's an insert operation)
            int rowsAffected = preparedStatement.executeUpdate();

            // Check if the operation was successful
            if (rowsAffected > 0) {
                System.out.println("Insert operation successful.");
            } else {
                System.out.println("Insert operation failed.");
            }
        }
    }

    @Override
    public Map<String, Object> findById(String id) {
        try {
            String sql = "SELECT * FROM MatierePremiere where id_reference = '" + id + "';";

            Statement statement = connection.createStatement();
            var rs = statement.executeQuery(sql);
            List<Map<String, Object>> result = new ArrayList<>();
            if (!rs.isBeforeFirst()) {
                // If result set is empty, return null or handle accordingly
                return null;
            }
            while (rs.next()) {
                Map<String, Object> resMap = new HashMap<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    resMap.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }
                result.add(resMap);
            }
            return result.getFirst();

        } catch (SQLException e) {
            System.out.println("Error collecting data from Facture");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> findByFactureId(String id) {
        try {
            String sql = "SELECT * FROM MatierePremiere where id_facture = '" + id + "';";

            Statement statement = connection.createStatement();
            var rs = statement.executeQuery(sql);
            List<Map<String, Object>> result = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> resMap = new HashMap<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    resMap.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }
                result.add(resMap);
            }
            return result;

        } catch (SQLException e) {
            System.out.println("Error collecting data from Facture");
            return null;
        }
    }
    public List<Map<String, Object>> getAllInfos(String matierePremiereId){
        try {
            String sql = "SELECT m.id_facture, f.date, m.px_unitaire, m.px_revient, m.quantite FROM MatierePremiere m, Facture f where id_reference = '" + matierePremiereId + "' and id = id_facture;";

            Statement statement = connection.createStatement();
            var rs = statement.executeQuery(sql);
            List<Map<String, Object>> result = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> resMap = new HashMap<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    resMap.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }
                result.add(resMap);
            }
            return result;

        } catch (SQLException e) {
            System.out.println("Error collecting data from Facture");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> findAll() {
        try {
            String sql = "SELECT * FROM MatierePremiere";

            Statement statement = connection.createStatement();
            var rs = statement.executeQuery(sql);
            List<Map<String, Object>> result = new ArrayList<>();

            while (rs.next()) {
                Map<String, Object> resMap = new HashMap<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    resMap.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }
                result.add(resMap);
            }
            return result;

        } catch (SQLException e) {
            System.out.println("Error connecting to SQLite database");
            return null;
        }
    }

    /*@Override
    public Entree update(Entree entree) {
        hashMapEntree.put(entree.getReference(), entree);
        return hashMapEntree.get(entree.getReference());
    }

    @Override
    public void delete(Entree entree) {
        hashMapEntree.remove(entree.getReference());
    }*/
}
