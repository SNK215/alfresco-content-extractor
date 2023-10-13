package org.example.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

public class Credentials {
    private String user;
    private String password;
    private String serviceUrl;
    private String destinationDirectory;
    private static final Logger logger = LogManager.getLogger();

    public Credentials() {
        File file  = new File("extractor_application.properties");
        try (InputStream input = new FileInputStream(file.getAbsolutePath())) {
            Properties prop = new Properties();
            prop.load(input);
            this.user = prop.getProperty("user");
            this.password = prop.getProperty("password");
            this.serviceUrl = prop.getProperty("serviceUrl");
            this.destinationDirectory = prop.getProperty("destinationDirectory");
        } catch (FileNotFoundException e) {
            logger.error("File " + file.getName() + " not found");
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("Error retrieving login data, please verify and modify extractor_application file");
            throw new RuntimeException(e);
        }
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getDestinationDirectory() {
        return destinationDirectory;
    }

    public void setDestinationDirectory(String destinationDirectory) {
        this.destinationDirectory = destinationDirectory;
    }
}
