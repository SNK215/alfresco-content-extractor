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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused"})
public class SessionGenerator {
    private String serviceUrl = "http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser";
    private String user = "admin";
    private String password = "admin";
    private Session session = null;
    protected static final Logger logger = LogManager.getLogger();


    public SessionGenerator() {
    }

    public Session generate() {
        try {
            if (session == null) {
                SessionFactory factory = SessionFactoryImpl.newInstance();
                Map<String, String> parameter = new HashMap<>();

                parameter.put(SessionParameter.USER, getUser());
                parameter.put(SessionParameter.PASSWORD, getPassword());

                parameter.put(SessionParameter.BROWSER_URL, getServiceUrl());
                parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());

                List<Repository> repositories = factory.getRepositories(parameter);

                this.session = repositories.get(0).createSession();
            }
        }
        catch (CmisObjectNotFoundException e) {
            logger.error("Incorrect serviceUrl, please change it and restart the app (" + serviceUrl + ")");
        }
        catch (CmisUnauthorizedException e) {
            logger.error("Incorrect username or password, please change it and restart the app");
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
