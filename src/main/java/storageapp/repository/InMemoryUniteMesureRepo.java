package storageapp.repository;

import storageapp.model.UniteMesureRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryUniteMesureRepo implements UniteMesureRepository {

    private final Connection connection;

    public InMemoryUniteMesureRepo(Connection connection){
        this.connection = connection;
    }
    @Override
    public void create(String uniteMesure) {
        String sql = "insert into UniteMesure values ('" + uniteMesure + "');";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Insert operation into UniteMesure successful.");
            } else {
                System.out.println("Insert operation into UniteMesure failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM UniteMesure ;";

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
            System.out.println("UniteMesure - findAll - Error connecting to SQLite database");
            return null;
        }
    }

    @Override
    public Map<String, Object> findById(String uniteMesure) {
        String sql = "SELECT * FROM UniteMesure where nom = '" + uniteMesure + "';";

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
            System.out.println("UniteMesure - findById - Error collecting data from UniteMesure");
            return null;
        }
    }

    @Override
    public void update(String uniteMesureInit, String newUniteMesure) {
        String sql = "UPDATE Sortie " +
                "SET nom = '" + newUniteMesure +"'" +
                "WHERE nom = '" + uniteMesureInit +"';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update operation into UniteMesure successful.");
            } else {
                System.out.println("Update operation into UniteMesure failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
