package storageapp.repository;

import storageapp.model.DeviseRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryDeviseRepo implements DeviseRepository {
    private final Connection connection;

    public InMemoryDeviseRepo(Connection connection){
        this.connection = connection;
    }
    @Override
    public void create(String devise) throws SQLException {
        String sql = "insert into Devise values ('" + devise + "');";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Insert operation into Devise successful.");
            } else {
                System.out.println("Insert operation into Devise failed.");
            }
        }
    }

    @Override
    public Map<String, Object> findById(String id) {
        String sql = "SELECT * FROM Devise where nom = '" + id + "';";

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
            System.out.println("Devise - findById - Error collecting data from Devise");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM Devise";

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
            System.out.println("Devise - findAll - Error connecting to SQLite database");
            return null;
        }
    }
}
