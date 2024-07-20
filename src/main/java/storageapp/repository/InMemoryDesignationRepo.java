package storageapp.repository;

import storageapp.model.DesignationRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryDesignationRepo implements DesignationRepository {
    private final Connection connection;

    public InMemoryDesignationRepo(Connection connection){
        this.connection = connection;
    }
    @Override
    public void create(String designation) throws SQLException {
        String sql = "insert into Designation values ('" + designation + "');";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Insert operation into Designation successful.");
            } else {
                System.out.println("Insert operation into Designation failed.");
            }
        }
    }

    @Override
    public Map<String, Object> findById(String id) {
        String sql = "SELECT * FROM Designation where nom = '" + id + "';";

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
            System.out.println("Designation - findById - Error collecting data from Designation");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM Designation";

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
            System.out.println("Designation - findAll - Error connecting to SQLite database");
            return null;
        }
    }
}
