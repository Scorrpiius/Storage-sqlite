package storageapp.repository;

import storageapp.model.CommandeRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryCommandeRepo implements CommandeRepository {
    private final Connection connection;
    public InMemoryCommandeRepo(Connection connection) { this.connection = connection;}
    @Override
    public void create(String id, String description, LocalDate date) {
        String sql = "insert into Commande values ('" + id + "', '" + date.toString() + "', '" + description + "');";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Insert operation into Commande successful.");
            } else {
                System.out.println("Insert operation into Commande failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM Commande";

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
            System.out.println("Commande - findAll - Error connecting to SQLite database");
            return null;
        }
    }

    @Override
    public Map<String, Object> findById(String id) {
        String sql = "SELECT * FROM Commande " +
                "WHERE id = '" + id + "';";

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
            System.out.println("Commande - findAll - Error connecting to SQLite database");
            return null;
        }
    }

    @Override
    public void updateCommandeId(String idCommandeInit, String idCommandeNouveau){
        String sql = "UPDATE Commande " +
                "SET id = '" + idCommandeNouveau +"'" +
                "WHERE id = '" + idCommandeInit +"';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("UpdateCommandeId operation into Commande successful.");
            } else {
                System.out.println("UpdateCommandeId operation into Commande failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
