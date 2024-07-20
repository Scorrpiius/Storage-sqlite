package storageapp.repository;

import storageapp.model.CategorieRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryCategorieRepo implements CategorieRepository {
    private final Connection connection;

    public InMemoryCategorieRepo(Connection connection){
        this.connection = connection;
    }
    @Override
    public void create(String categorie) throws SQLException {
        String sql = "insert into Categorie values ('" + categorie + "');";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Insert operation into Categorie successful.");
            } else {
                System.out.println("Insert operation into Categorie failed.");
            }
        }
    }

    @Override
    public Map<String, Object> findById(String id) {
        String sql = "SELECT * FROM Categorie where nom = '" + id + "';";

        try {
            Statement statement = connection.createStatement();
            var rs = statement.executeQuery(sql);
            if (!rs.isBeforeFirst()) {
                // If result set is empty, return null or handle accordingly
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
            System.out.println("Categorie - findById - Error collecting data from Categorie");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM Categorie";

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
            System.out.println("Categorie - findAll - Error connecting to SQLite database");
            return null;
        }
    }
}
