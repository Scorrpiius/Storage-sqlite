package storageapp.repository;

import storageapp.model.CommandeMatierePremiereRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryCommandeMatierePremiereRepo implements CommandeMatierePremiereRepository {
    private final Connection connection;
    public InMemoryCommandeMatierePremiereRepo(Connection connection){this.connection = connection;}

    @Override
    public void create(String idCommande, String idMatierePremiere, int quantite) {
        String sql = "insert into Commande_MatierePremiere values ('" + idCommande + "', '" + idMatierePremiere + "', " + quantite + ");";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Insert operation into Commande_MatierePremiere successful.");
            } else {
                System.out.println("Insert operation into Commande_MatierePremiere failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM Commande_MatierePremiere";

        try {
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
            System.out.println("Commande_MatierePremiere - findAll - Error connecting to SQLite database");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> findByCommandeId(String idCommande) {
        String sql = "SELECT * FROM Commande_MatierePremiere " +
                "WHERE id_Commande = '" + idCommande + "';";

        try {
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
            System.out.println("Commande_MatierePremiere - findAll - Error connecting to SQLite database");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> findByMatiereId(String idMatierePremiere) {
        String sql = "SELECT id_Commande FROM Commande_MatierePremiere " +
                "WHERE id_MatierePremiere = '" + idMatierePremiere + "';";

        try {
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
            System.out.println("Commande_MatierePremiere - findAll - Error connecting to SQLite database");
            return null;
        }
    }

    @Override
    public Map<String, Object> findByIds(String idCommande, String idMatierePremiere) {
        String sql = "SELECT * FROM Commande_MatierePremiere " +
                "WHERE id_Commande = '" + idCommande + "' " +
                "AND id_MatierePremiere = '" + idMatierePremiere + "';";

        try {
            Statement statement = connection.createStatement();
            var rs = statement.executeQuery(sql);
            List<Map<String, Object>> result = new ArrayList<>();
            if (!rs.isBeforeFirst()) {
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
            System.out.println("Commande_MatierePremiere - findAll - Error connecting to SQLite database");
            return null;
        }
    }

    @Override
    public void updateQuantite(String idCommande, String idMatierePremiere, int quantite){
        String sql = "UPDATE Commande_MatierePremiere " +
                "SET quantite = " + quantite +
                " WHERE id_Commande = '" + idCommande + "' and id_MatierePremiere = '" + idMatierePremiere + "';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update operation into Commande_MatierePremiere successful.");
            } else {
                System.out.println("Update operation into Commande_MatierePremiere failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
