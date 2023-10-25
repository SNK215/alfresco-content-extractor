package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.Properties;

/**
 * Class used to retrieve and store some parameters that are needed in order for the extraction to take place. Singleton design pattern is applied
 */

@Log4j2
public class Credentials {

    /**
     * Alfresco admin username
     */
    @Getter
    @Setter
    private static String user;

    /**
     * Alfresco admin password
     */
    @Getter
    @Setter
    private static String password;

    /**
     * service URL of an Alfresco repository
     */
    @Getter
    @Setter
    private static String serviceUrl;

    /**
     * Directory where the files and folders will be imported
     */
    @Getter
    @Setter
    private static String destinationDirectory;

    /**
     * Instance of the class
     */
    private static Credentials instance;

    public Credentials() {

    }

    /**
     *  Data is recovered from extractor_application.properties and stored in an instance of the class
     */
    public void init() {
        File file  = new File("extractor_application.properties");
        try (InputStream input = new FileInputStream(file.getAbsolutePath())) {
            Properties prop = new Properties();
            prop.load(input);
            user = prop.getProperty("user");
            password = prop.getProperty("password");
            serviceUrl = prop.getProperty("serviceUrl");
            destinationDirectory = prop.getProperty("destinationDirectory");
            log.info("Params retrieved from extractor_application.properties successfully");
        } catch (FileNotFoundException e) {
            log.error("File " + file.getName() + " not found");
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("Error retrieving login data, please verify and modify extractor_application.properties");
            throw new RuntimeException(e);
        }
    }
    public void resetPropertyValues() {
        File file = new File("extractor_application.properties");
        try (OutputStream output = new FileOutputStream(file.getAbsolutePath())) {
            Properties prop = new Properties();

            // Modification des propriétés
            prop.setProperty("user", "");
            prop.setProperty("password", "");
            prop.setProperty("serviceUrl", serviceUrl);
            prop.setProperty("destinationDirectory", destinationDirectory);

            // Enregistrement des modifications dans le fichier sans la ligne de commentaire
            for (String key : prop.stringPropertyNames()) {
                String value = prop.getProperty(key);
                output.write((key + "=" + value + "\n").getBytes("UTF-8"));
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement des modifications", e);
        }
    }


    /**
     * @return instance of the Credentials class
     */
    public static Credentials getInstance() {
        if (instance == null) {
            instance = new Credentials();
        }
        return instance;
    }
}
