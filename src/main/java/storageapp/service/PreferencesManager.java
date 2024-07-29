package storageapp.service;

import java.io.*;
import java.util.Properties;

public class PreferencesManager {
    private static final String CONFIG_FILE = "config.properties";
    private Properties properties = new Properties();

    public PreferencesManager() {
        loadProperties();
    }

    private void loadProperties() {
        File configFile = new File(CONFIG_FILE);
        if (configFile.exists()) {
            try (InputStream input = new FileInputStream(configFile)) {
                properties.load(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getDbPath() {
        return properties.getProperty("dbPath");
    }

    public void setDbPath(String path) {
        properties.setProperty("dbPath", path);
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}