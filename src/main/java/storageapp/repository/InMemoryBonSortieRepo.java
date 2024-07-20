package storageapp.repository;

import storageapp.model.BonSortieRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryBonSortieRepo implements BonSortieRepository {
    private final Connection connection;
    public InMemoryBonSortieRepo(Connection connection){
        this.connection = connection;
    }
    @Override
    public void create(String id, LocalDate date) {
        String sql = "insert into BonSortie values ('" + id  + "', '" + date.toString() + "');";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Insert operation into BonSortie successful.");
            } else {
                System.out.println("Insert operation into BonSortie failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM BonSortie";

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
            System.out.println("BonSortie - findAll - Error connecting to SQLite database");
            return null;
        }
    }

    @Override
    public Map<String, Object> findById(String id) {
        String sql = "SELECT * FROM BonSortie where id = '" + id +"';";

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
            return result.getFirst();

        } catch (SQLException e) {
            System.out.println("BonSortie - findById - Error connecting to SQLite database");
            return null;
        }
    }
    @Override
    public void update(String idInitial,String idFinal, LocalDate nouvelleDate) {
        String sql = "UPDATE BonSortie SET id = '" + idFinal +"', " +
                "date = '" + nouvelleDate.toString() + "' " +
                "WHERE id = '" + idInitial +"';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update operation into BonSortie successful.");
            } else {
                System.out.println("Update operation into BonSortie failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
