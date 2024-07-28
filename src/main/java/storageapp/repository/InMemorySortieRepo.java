package storageapp.repository;

import storageapp.model.SortieRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemorySortieRepo implements SortieRepository {
    private final Connection connection;

    public InMemorySortieRepo(Connection connection){ this.connection = connection; }
    @Override
    public void create(int id, String id_bonSortie, String id_matierePremiere, String description, int quantite) {
        String sql = "insert into Sortie values (" + id  + ", " + quantite + ", '" + id_bonSortie + "', '" + id_matierePremiere + "', '" + description + "');";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Insert operation into Sortie successful.");
            } else {
                System.out.println("Insert operation into Sortie failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM Sortie";

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
            System.out.println("Sortie - findAll - Error connecting to SQLite database");
            return null;
        }
    }

    @Override
    public Map<String, Object> findByIds(String idMatPrem, String id_bonSortie) {
        String sql = "SELECT * FROM Sortie where id_matierePremiere = '" + idMatPrem +
                "' and id_bonSortie = '" + id_bonSortie + "';";

        try {
            Statement statement = connection.createStatement();
            var rs = statement.executeQuery(sql);
            if (!rs.isBeforeFirst()) {
                return null;
            }
            List<Map<String, Object>> result = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> resMap = new HashMap<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    resMap.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }
                result.add(resMap);
            }
            return result.getFirst();

        } catch (SQLException e) {
            System.out.println("Sortie - findByIds - Error collecting data from Sortie");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> getAllSortiesByBon(String id_bonSortie) {
        String sql = "SELECT * FROM Sortie where id_bonSortie = '" + id_bonSortie +"';";
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
            System.out.println("Sortie - getAllSortiesByBon - Error connecting to SQLite database");
            return null;
        }
    }

    @Override
    public void update(int id, String idBon, int quantite, String reference, String description){
        StringBuilder sqlBuilder = new StringBuilder("update Sortie set");
        if (quantite != 0) {
            sqlBuilder.append(" quantite = ").append(quantite).append(",");
        }
        if (reference != null) {
            sqlBuilder.append(" id_matierePremiere = '").append(reference).append("',");
        }
        if (description != null) {
            sqlBuilder.append(" description = '").append(description).append("',");
        }
        // Remove the trailing comma if it exists
        if (sqlBuilder.charAt(sqlBuilder.length() - 1) == ',') {
            sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        }
        sqlBuilder.append(" where id = '").append(id).append("' and ");
        sqlBuilder.append(" id_bonSortie = '").append(idBon).append("'; ");
        String sql = sqlBuilder.toString();

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update operation into Sortie successful.");
            } else {
                System.out.println("Update operation into Sortie failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateBonId(String idBonInit,String idBonNouveau) {
        String sql = "UPDATE Sortie " +
                "SET id_bonSortie = '" + idBonNouveau +"' " +
                "WHERE id_bonSortie = '" + idBonInit +"';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("UpdateBonId operation into Sortie successful.");
            } else {
                System.out.println("BonId operation into Sortie failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String idMatPremiere, String idBonSortie){
        String sql = "DELETE FROM Sortie " +
                " WHERE id_matierePremiere = '" + idMatPremiere + "' and id_bonSortie = '" + idBonSortie + "';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Delete operation into Sortie successful.");
            } else {
                System.out.println("Delete operation into Sortie failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
