package storageapp.repository;


import storageapp.model.FournisseurRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryFournisseurRepo implements FournisseurRepository {
    private final Connection connection;

    public InMemoryFournisseurRepo(Connection connection){
        this.connection = connection;
    }
    @Override
    public void create(String fournisseur) throws SQLException {
        String sql = "insert into Fournisseur values ('" + fournisseur + "');";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Insert operation into Fournisseur successful.");
            } else {
                System.out.println("Insert operation into Fournisseur failed.");
            }
        }
    }

    @Override
    public Map<String, Object> findById(String id) {
        String sql = "SELECT * FROM Fournisseur where nom = '" + id + "';";

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
            System.out.println("Fournisseur - findById - Error collecting data from Fournisseur");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM Fournisseur ;";

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
            System.out.println("Fournisseur - findAll - Error connecting to SQLite database");
            return null;
        }
    }
}
