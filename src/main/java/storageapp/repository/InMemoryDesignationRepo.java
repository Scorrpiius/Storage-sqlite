package storageapp.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryDesignationRepository implements DesignationRepository{
    private Connection connection;

    public InMemoryDesignationRepository(Connection connection){
        this.connection = connection;
    }
    @Override
    public void create(String designation) throws SQLException {
        String sql = "insert into Designation values ('" + designation + "');";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Execute the update (since it's an insert operation)
            int rowsAffected = preparedStatement.executeUpdate();

            // Check if the operation was successful
            if (rowsAffected > 0) {
                System.out.println("Insert operation into Designation successful.");
            } else {
                System.out.println("Insert operation into Designation failed.");
            }
        }
    }

    @Override
    public Map<String, Object> findById(String id) {
        try {
            String sql = "SELECT * FROM Designation where nom = '" + id + "';";

            Statement statement = connection.createStatement();
            var rs = statement.executeQuery(sql);
            if (!rs.first()) return null;
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
            System.out.println("Error collecting data from FicheStock");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> findAll() {
        try {
            String sql = "SELECT * FROM Designation";

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
}
