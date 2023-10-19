package org.example.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.Properties;

/**
 * This class is used to retrieve and store some parameters that are needed in order for the extraction to take place
 */
@Getter
@Setter
@Log4j2
public class Credentials {

    private String user;
    private String password;
    private String serviceUrl;
    private String destinationDirectory;
    private static Credentials instance;

    public Credentials() {
        init();
    }

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

    //Singleton
    public static Credentials getInstance() {
        if (instance == null) {
            instance = new Credentials();
        }
        return instance;
    }
}
