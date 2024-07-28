package storageapp.repository;

import storageapp.model.FactureRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryFactureRepo implements FactureRepository {
    private final Connection connection;
    public InMemoryFactureRepo(Connection connection){
        this.connection = connection;
    }

    @Override
    public void create(String id, LocalDate date, String supplier, double tauxTheo, double tauxReel, double valeur_devise, String nom_devise, int nbrMatPrem) throws SQLException {
        String sql = "insert into Facture values " +
                "('" + id + "'," +
                " '" + supplier + "'," +
                " '" + date.toString() + "'," +
                " " + valeur_devise + "," +
                " '" + nom_devise + "'," +
                " " + tauxReel + "," +
                " " + tauxTheo + "," +
                " " + nbrMatPrem +");";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Insert operation into Facture successful.");
            } else {
                System.out.println("Insert operation into Facture failed.");
            }
        }
    }

    @Override
    public Map<String, Object> findById(String id) {
        String sql = "SELECT * FROM Facture where id = '" + id + "';";

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
            System.out.println("Facture - findById - Error collecting data from Facture");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM Facture";

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
            System.out.println("Facture - findAll - Error connecting to SQLite database");
            return null;
        }
    }

    @Override
    public void update(String idInit, String idNouveau, LocalDate date, String supplier, double tauxTheo, double tauxReel, double valeur_devise, String nom_devise, int nbrMatPrem) {
        String sql = "UPDATE Facture " +
                "SET id = '" + idNouveau +"'," +
                "fournisseur = '" + supplier + "'," +
                "date = '" + date +"'," +
                " valeur_devise = " + valeur_devise + "," +
                " nom_devise = '" + nom_devise + "'," +
                " tx_reel = "+ tauxReel + ", " +
                " tx_theo = " + tauxTheo + ", " +
                " nbr_matPrem = " + nbrMatPrem +
                " WHERE id = '" + idInit +"';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update operation into Facture successful.");
            } else {
                System.out.println("Update operation into Facture failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




    @Override
    public void updateFactureId(String idFactureInit, String idFactureNouveau){
        String sql = "UPDATE Facture " +
                "SET id = '" + idFactureNouveau +"'" +
                "WHERE id = '" + idFactureInit +"';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update operation into Facture successful.");
            } else {
                System.out.println("Update operation into Facture failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateOtherInfo(String idFacture, LocalDate date, String fournisseur, double tauxTheo, double tauxReel, double valeurDevise, String nomDevise, int nbrMatPrem){
        String sql = "UPDATE Facture " +
                "SET fournisseur = '" + fournisseur + "'," +
                "date = '" + date +"'," +
                "valeur_devise = " + valeurDevise + "," +
                "nom_devise = '" + nomDevise + "'," +
                "tx_reel = "+ tauxReel + ", " +
                "tx_theo = " + tauxTheo + ", " +
                "nbr_matPrem = " + nbrMatPrem + " " +
                "WHERE id = '" + idFacture +"';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update operation into Facture successful.");
            } else {
                System.out.println("Update operation into Facture failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
