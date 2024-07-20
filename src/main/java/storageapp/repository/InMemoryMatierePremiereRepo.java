
package storageapp.repository;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import storageapp.model.MatierePremiereRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryMatierePremiereRepo implements MatierePremiereRepository {
    private final Connection connection;

    public InMemoryMatierePremiereRepo(Connection connection){
        this.connection = connection;
    }

    private byte[] readFile(String file) {
        ByteArrayOutputStream bos = null;
        File f = new File(file);
        try(FileInputStream fis = new FileInputStream(f)) {
            byte[] buffer = new byte[1024];
            bos = new ByteArrayOutputStream();
            for (int len; (len = fis.read(buffer)) != -1; ) {
                bos.write(buffer, 0, len);

            }
        } catch (IOException e2) {
            System.err.println(e2.getMessage());
        }
        return bos != null ? bos.toByteArray() : null;
    }

    @Override
    public void create(String idRef, String idFacture, int quantite, double px_unitaire, double px_revient_devise, double px_revient_local, String categorie, String designation, String uniteMesure, String file) throws SQLException{
        String sql = "";
        if(file == null){
            sql = "insert into MatierePremiere values " +
                    "('" + idRef + "', " +
                    "'" + idFacture + "'," +
                    " " + quantite + "," +
                    " " + px_unitaire + "," +
                    " " + px_revient_devise + "," +
                    " " + px_revient_local +", " +
                    "'" + categorie +"', " +
                    "'" + designation +"', " +
                    "'" + uniteMesure+ "'," +
                    " null );";
        } else {
            sql = "insert into MatierePremiere values " +
                    "('" + idRef + "'," +
                    "'" + idFacture + "'," +
                    " " + quantite + "," +
                    " " + px_unitaire + "," +
                    " " + px_revient_devise + "," +
                    " " + px_revient_local + "," +
                    " '" + categorie +"'," +
                    " '" + designation +"'," +
                    " '" + uniteMesure + "'," +
                    " ? );";
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if(file != null) preparedStatement.setBytes(1, readFile(file));
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Insert operation into MatierePremiere successful.");
            } else {
                System.out.println("Insert operation into MatierePremiere failed.");
            }
        }
    }

    @Override
    public Map<String, Object> findById(String id) {
        String sql = "SELECT * FROM MatierePremiere where id_reference = '" + id + "';";

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
            System.out.println("Matiere premiere - findById - Error collecting data from MatierePremiere");
            return null;
        }
    }

    @Override
    public List<Map<String, Object>> findByFactureId(String id) {
        String sql = "SELECT * FROM MatierePremiere where id_facture = '" + id + "';";

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
            System.out.println("Matiere Premiere - findByFactureId - Error collecting data from MatierePremiere");
            return null;
        }
    }
    @Override
    public List<Map<String, Object>> getAllInfos(String matierePremiereId){
        String sql = "SELECT m.id_facture, f.date, m.px_unitaire, m.px_revient_devise, m.px_revient_local, m.quantite FROM MatierePremiere m, Facture f where id_reference = '" + matierePremiereId + "' and id = id_facture;";

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
            System.out.println("Matiere Premiere - getAllInfos - Error collecting data from MatierePremiere");
            return null;
        }
    }

    @Override
    public void update(String idFactureInit, String idFactureNouveaux,  double tx, double devise) {
        String sql = "UPDATE MatierePremiere " +
                "SET id_facture = '" + idFactureNouveaux +"', " +
                "px_revient_devise = px_unitaire + " + tx + "* px_unitaire, " +
                "px_revient_local = px_unitaire * " + devise + " + " + tx * devise + "* px_unitaire " +
                "WHERE id_facture = '" + idFactureInit +"';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Execute the update (since it's an insert operation)
            int rowsAffected = preparedStatement.executeUpdate();

            // Check if the operation was successful
            if (rowsAffected > 0) {
                System.out.println("Update operation into MatierePremiere successful.");
            } else {
                System.out.println("Update operation into MatierePremiere failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getPicture(String id, String filename, ImageView imageView) {
        String selectSQL = "SELECT img FROM MatierePremiere  where id_reference = '" + id + "';";
        ResultSet rs = null;
        FileOutputStream fos = null;
        PreparedStatement pstmt = null;

        try {
            pstmt = connection.prepareStatement(selectSQL);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                InputStream input = rs.getBinaryStream("img");
                if(input == null) return;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                byte[] imageBytes = byteArrayOutputStream.toByteArray();

                ByteArrayInputStream inStreambj = new ByteArrayInputStream(imageBytes);
                BufferedImage newImage = ImageIO.read(inStreambj);
                imageView.setImage(SwingFXUtils.toFXImage(newImage, null));
            }
        } catch (SQLException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> update(String factureId, String refInit, String newRef, String categorie, String designation, String quantite, String pxUnit, String uniteMesure){
        int qte = Integer.parseInt(quantite);
        double pxUnitaire = Double.parseDouble(pxUnit);

        String sql = "UPDATE MatierePremiere " +
                "SET id_reference = '" + newRef +"'," +
                "categorie = '" + categorie + "'," +
                "designation = '" + designation +"'," +
                " quantite = " + qte + "," +
                " px_unitaire = " + pxUnitaire + "," +
                " uniteMesure = '"+ uniteMesure + "' " +
                "WHERE id_reference = '" + refInit +"' and id_facture = '" + factureId + "';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Update operation into MatierePremiere successful.");
            } else {
                System.out.println("Update operation into MatierePremiere failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public List<Map<String, Object>> findAll() {
        String sql = "SELECT * FROM MatierePremiere";

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
            System.out.println("Matiere Premiere - findAll - Error connecting to SQLite database");
            return null;
        }
    }
    @Override
    public Map<String, Object> findByIds(String factureId, String matiereId){
        String sql = "SELECT * FROM MatierePremiere where id_facture = '" + factureId + "' and id_reference = '" + matiereId + "';";

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
            System.out.println("Matiere Premiere - findByFactureId - Error collecting data from MatierePremiere");
            return null;
        }

    }

    @Override
    public void delete(String idRef, String idFacture){
        String sql = "DELETE FROM MatierePremiere " +
                " WHERE id_reference = '" + idRef + "' and id_facture = '" + idFacture + "';";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Delete operation into MatierePremiere successful.");
            } else {
                System.out.println("Delete operation into MatierePremiere failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
