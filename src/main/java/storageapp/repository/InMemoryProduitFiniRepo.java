package storageapp.repository;

import storageapp.model.ProduitFiniRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryProduitFiniRepo implements ProduitFiniRepository {
    private final Connection connection;
    public InMemoryProduitFiniRepo(Connection connection){ this.connection = connection;}

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
    public void create(String reference, String file){
        String sql = "";
        if(file == null){
            sql = "insert into ProduitFini values ('" + reference + "'," + null + ");";
        } else {
            sql = "insert into MatierePremiere values ('" + reference + "', ? );";
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if(file != null) preparedStatement.setBytes(1, readFile(file));
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Insert operation into MatierePremiere successful.");
            } else {
                System.out.println("Insert operation into MatierePremiere failed.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, Object>> findAll(){
        String sql = "SELECT * FROM ProduitFini";

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
            System.out.println("Produit Fini - findAll - Error connecting to SQLite database");
            return null;
        }
    }
}
