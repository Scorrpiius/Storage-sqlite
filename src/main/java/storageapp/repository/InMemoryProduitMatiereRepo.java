package storageapp.repository;

import storageapp.model.ProduitFini_MatierePremiereRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryProduitMatiereRepo implements ProduitFini_MatierePremiereRepository {
    private final Connection connection;
    public InMemoryProduitMatiereRepo(Connection connection) { this.connection = connection;}
    @Override
    public void create(String produitReference, String matiereReference, int quantite){
        String sql = "insert into ProduitFini_MatierePremiere values ('" + produitReference  + "', '" + matiereReference + "', + " + quantite +");";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Insert operation into ProduitFini_MatierePremiere successful.");
            } else {
                System.out.println("Insert operation into ProduitFini_MatierePremiere failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, Object>> findByProduitId(String produitId){
        String sql = "SELECT * FROM ProduitFini_MatierePremiere where produit_reference = '" + produitId + "';";

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
            return result;

        } catch (SQLException e) {
            System.out.println("ProduitFini_MatierePremiere - findByProduitId - Error collecting data from ProduitFini_MatierePremiere");
            return null;
        }
    }

    @Override
    public Map<String, Object> findByIds(String produitId, String matiereId){
        String sql = "SELECT * FROM ProduitFini_MatierePremiere where produit_reference = '" + produitId + "' and matiere_id = '" + matiereId + "';";

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
            System.out.println("ProduitFini_MatierePremiere - findByProduitId - Error collecting data from ProduitFini_MatierePremiere");
            return null;
        }
    }
    @Override
    public void updateQuantite(String produitId, String matiereId, int quantite){
        String sql = "UPDATE ProduitFini_MatierePremiere " +
                "SET quantite = " + quantite +
                " WHERE produit_reference = '" + produitId + "' and matiere_id = '" + matiereId + "';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update operation into ProduitFini_MatierePremiere successful.");
            } else {
                System.out.println("Update operation into ProduitFini_MatierePremiere failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(String produitId, String matiereInit, String newMatiere, String quantite){
        StringBuilder sqlBuilder = new StringBuilder("update ProduitFini_MatierePremiere set");
        if (newMatiere != null) {
            sqlBuilder.append(" matiere_id = '").append(newMatiere).append("',");
        }
        if (quantite != null) {
            sqlBuilder.append(" quantite = '").append(quantite).append("',");
        }
        // Remove the trailing comma if it exists
        if (sqlBuilder.charAt(sqlBuilder.length() - 1) == ',') {
            sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        }

        sqlBuilder.append(" where matiere_id = '").append(matiereInit).append("'");
        sqlBuilder.append(" and produit_reference = '").append(produitId).append("';");
        String sql = sqlBuilder.toString();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update operation into ProduitFini_MatierePremiere successful.");
            } else {
                System.out.println("Update operation into ProduitFini_MatierePremiere failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void delete(String produitId, String matiereId){
        String sql = "DELETE FROM ProduitFini_MatierePremiere " +
                " WHERE produit_reference = '" + produitId + "' and matiere_id = '" + matiereId + "';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Delete operation into ProduitFini_MatierePremiere successful.");
            } else {
                System.out.println("Delete operation into ProduitFini_MatierePremiere failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll(String produitId){
        String sql = "DELETE FROM ProduitFini_MatierePremiere " +
                " WHERE produit_reference = '" + produitId + "';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("DeleteAll operation into ProduitFini_MatierePremiere successful.");
            } else {
                System.out.println("DeleteAll operation into ProduitFini_MatierePremiere failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateProduitId(String idProduitInit, String idProduitNouveau){
        String sql = "UPDATE ProduitFini_MatierePremiere " +
                "SET produit_reference = '" + idProduitNouveau + "' " +
                "WHERE produit_reference = '" + idProduitInit +"';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("UpdateProduitId operation into ProduitFini_MatierePremiere successful.");
            } else {
                System.out.println("UpdateProduitId operation into ProduitFini_MatierePremiere failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
