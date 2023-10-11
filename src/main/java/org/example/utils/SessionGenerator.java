package org.example.utils;

import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisUnauthorizedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Credentials;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings({"unused"})
public class SessionGenerator {
    private String serviceUrl = "http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser";
    private String user = "admin";
    private String password = "admin";
    private Session session = null;
    protected static final Logger logger = LogManager.getLogger();


    public SessionGenerator() {
    }

    public Credentials getCredentials() {
        Credentials credentials = new Credentials();
        File file = new File("extractor_application.properties");
        try (InputStream input = new FileInputStream(file.getAbsolutePath())) {
            Properties prop = new Properties();
            prop.load(input);
            credentials.setUser(prop.getProperty("user"));
            credentials.setPassword(prop.getProperty("password"));
            credentials.setServiceUrl(prop.getProperty("serviceUrl"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return credentials;
    }
    public Session generate() {
        try {
            if (session == null) {
                SessionFactory factory = SessionFactoryImpl.newInstance();
                Map<String, String> parameter = new HashMap<>();

                parameter.put(SessionParameter.USER, getCredentials().getUser());
                parameter.put(SessionParameter.PASSWORD, getCredentials().getPassword());
                parameter.put(SessionParameter.BROWSER_URL, getCredentials().getServiceUrl());
                parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());

                List<Repository> repositories = factory.getRepositories(parameter);

                this.session = repositories.get(0).createSession();
            }
        }
        catch (CmisObjectNotFoundException e) {
            logger.error("Incorrect serviceUrl, please change it and restart the app (" + serviceUrl + ")");
            throw new CmisObjectNotFoundException();
        }
        catch (CmisUnauthorizedException e) {
            logger.error("Incorrect username or password, please change it and restart the app");
            throw new CmisUnauthorizedException();
        }
        return this.session;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
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
}
