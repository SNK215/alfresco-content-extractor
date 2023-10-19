package org.example.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

@Getter
@Setter
public class Credentials {

    private String user;
    private String password;
    private String serviceUrl;
    private String destinationDirectory;
    private static final Logger logger = LogManager.getLogger();
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
            logger.info("Params retrieved from extractor_application.properties successfully");
        } catch (FileNotFoundException e) {
            logger.error("File " + file.getName() + " not found");
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Error retrieving login data, please verify and modify extractor_application.properties");
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
