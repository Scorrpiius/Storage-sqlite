package storageapp.repository;


import storageapp.model.FicheStockRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryFicheStockRepo implements FicheStockRepository {
    private final Connection connection;

    public InMemoryFicheStockRepo(Connection connection){
        this.connection = connection;
    }

    @Override
    public void create(String id, String categorie, String designation, int quantite, String uniteMesure) throws SQLException {
        String sql = "insert into FicheStock values " +
                "('" + id + "'," +
                " '" + categorie + "'," +
                " '" + designation + "'," +
                " " + quantite + "," +
                " '" + uniteMesure +"');";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Insert operation in FicheStock successful.");
            } else {
                System.out.println("Insert operation in FicheStock failed.");
            }
        }
    }

    @Override
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM FicheStock";

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
            System.out.println("Fiche Stock - findAll - Error connecting to SQLite database");
            return null;
        }
    }

    @Override
    public Map<String, Object> findById(String id) {
        String sql = "SELECT * FROM FicheStock where id_matierePremiere = '" + id + "';";

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
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
            System.out.println("Fiche Stock - findById - Error collecting data from FicheStock");
            return null;
        }
    }

    @Override
    public void update(String referenceInit, String newReference, Integer nouvelleQte, String categorie, String designation) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("update FicheStock set");
        if (nouvelleQte != null) {
            sqlBuilder.append(" quantite = ").append(nouvelleQte).append(",");
        }
        if (newReference != null) {
            sqlBuilder.append(" id_matierePremiere = '").append(newReference).append("',");
        }
        if (categorie != null) {
            sqlBuilder.append(" categorie = '").append(categorie).append("',");
        }
        if (designation != null) {
            sqlBuilder.append(" designation = '").append(designation).append("',");
        }
        // Remove the trailing comma if it exists
        if (sqlBuilder.charAt(sqlBuilder.length() - 1) == ',') {
            sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        }

        sqlBuilder.append(" where id_matierePremiere = '").append(referenceInit).append("';");
        String sql = sqlBuilder.toString();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update operation into Fiche Stock successful.");
            } else {
                System.out.println("Update operation into Fiche Stock failed.");
            }
        }
    }

    @Override
    public List<Map<String, Object>> filter(String categorie, String designation, String reference) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM FicheStock");

        if (reference != null || designation != null || categorie != null) { sqlBuilder.append(" WHERE "); }
        if (reference != null) { sqlBuilder.append("id_matierePremiere = '").append(reference).append("'"); }
        if (designation != null) {
            if (reference != null) { sqlBuilder.append(" AND "); }
            sqlBuilder.append("designation = '").append(designation).append("'");
        }
        if (categorie != null) {
            if (reference != null || designation != null) { sqlBuilder.append(" AND "); }
            sqlBuilder.append("categorie = '").append(categorie).append("'");
        }
        String sql = sqlBuilder.toString();

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
            System.out.println("Fiche Stock - filter - Error collecting data from FicheStock");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> getAllId() {
        String sql = "SELECT id_matierePremiere FROM FicheStock;";

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
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
            System.out.println("Fiche Stock - getAllId - Error collecting data from FicheStock");
            return null;
        }
    }

    @Override
    public void updateQuantite(String reference, Integer quantite){

        String sql = "UPDATE FicheStock SET quantite = " + quantite + " where id_matierePremiere = '" + reference + "';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update operation into Fiche Stock successful.");
            } else {
                System.out.println("Update operation into Fiche Stock failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
