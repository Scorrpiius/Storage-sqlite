package storageapp.repository;

import storageapp.model.CommandeProduitFiniRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryCommandeProduitRepo implements CommandeProduitFiniRepository {
    private final Connection connection;
    public InMemoryCommandeProduitRepo(Connection connection){this.connection = connection;}

    @Override
    public void create(String idCommande, String idProduit, int quantite) {
        String sql = "insert into Commande_ProduitFini values ('" + idCommande + "', '" + idProduit + "', " + quantite + ");";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Insert operation into Commande_ProduitFini successful.");
            } else {
                System.out.println("Insert operation into Commande_ProduitFini failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM Commande_ProduitFini";

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
            System.out.println("Commande_ProduitFini - findAll - Error connecting to SQLite database");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> findByCommandeId(String idCommande) {
        String sql = "SELECT * FROM Commande_ProduitFini " +
                "WHERE id_Commande = '" + idCommande + "';";

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
            System.out.println("Commande_ProduitFini - findAll - Error connecting to SQLite database");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> findByProduitId(String idProduit) {
        String sql = "SELECT id_Commande FROM Commande_ProduitFini " +
                "WHERE id_ProduitFini = '" + idProduit + "';";

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
            System.out.println("Commande_ProduitFini - findAll - Error connecting to SQLite database");
            return null;
        }
    }

    @Override
    public Map<String, Object> findByIds(String idCommande, String idProduit) {
        String sql = "SELECT * FROM Commande_ProduitFini " +
                "WHERE id_Commande = '" + idCommande + "' " +
                "AND id_ProduitFini = '" + idProduit + "';";

        try {
            Statement statement = connection.createStatement();
            var rs = statement.executeQuery(sql);
            List<Map<String, Object>> result = new ArrayList<>();
            if (!rs.isBeforeFirst()) {
                return null;
            }

            while (rs.next()) {
                Map<String, Object> resMap = new HashMap<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    resMap.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }
                result.add(resMap);
            }
            return result.getFirst();

        } catch (SQLException e) {
            System.out.println("Commande_ProduitFini - findAll - Error connecting to SQLite database");
            return null;
        }
    }

    @Override
    public void updateQuantite(String idCommande, String idProduit, int quantite){
        String sql = "UPDATE Commande_ProduitFini " +
                "SET quantite = " + quantite +
                " WHERE id_Commande = '" + idCommande + "' and id_ProduitFini = '" + idProduit + "';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update operation into Commande_ProduitFini successful.");
            } else {
                System.out.println("Update operation into Commande_ProduitFini failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(String idCommande, String idProduit){
        String sql = "DELETE FROM Commande_ProduitFini " +
                " WHERE id_Commande = '" + idCommande + "' and id_ProduitFini = '" + idProduit + "';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Delete operation into Commande_ProduitFini successful.");
            } else {
                System.out.println("Delete operation into Commande_ProduitFini failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(String idCommande, String idAncienProduit, String idNouveauProduit, String quantite) {
        StringBuilder sqlBuilder = new StringBuilder("update Commande_ProduitFini set");
        if (quantite != null) {
            sqlBuilder.append(" quantite = '").append(quantite).append("',");
        }
        if (idNouveauProduit != null) {
            sqlBuilder.append(" id_ProduitFini = '").append(idNouveauProduit).append("',");
        }
        // Remove the trailing comma if it exists
        if (sqlBuilder.charAt(sqlBuilder.length() - 1) == ',') {
            sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        }

        sqlBuilder.append(" where id_Commande = '").append(idCommande).append("' and ");
        sqlBuilder.append("id_ProduitFini = '").append(idAncienProduit).append("'; ");
        String sql = sqlBuilder.toString();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update operation into Commande_ProduitFini successful.");
            } else {
                System.out.println("Update operation into Commande_ProduitFini failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateCommandeId(String idCommandeInit, String idCommandeNouveau){
        String sql = "UPDATE Commande_ProduitFini " +
                "SET id_Commande = '" + idCommandeNouveau +"'" +
                "WHERE id_Commande = '" + idCommandeInit +"';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("UpdateCommandeId operation into Commande_ProduitFini successful.");
            } else {
                System.out.println("UpdateCommandeId operation into Commande_ProduitFini failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateProduitId(String idProduitInit, String idProduitNouveau){
        String sql = "UPDATE Commande_ProduitFini " +
                "SET id_ProduitFini = '" + idProduitNouveau +"'" +
                "WHERE id_ProduitFini = '" + idProduitInit +"';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("UpdateProduitId operation into Commande_ProduitFini successful.");
            } else {
                System.out.println("UpdateProduitId operation into Commande_ProduitFini failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
