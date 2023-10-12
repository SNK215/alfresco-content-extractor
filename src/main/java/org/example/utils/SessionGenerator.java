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
    private Session session = null;
    Credentials credentials = new Credentials();
    private static final Logger logger = LogManager.getLogger();

    public SessionGenerator() {
    }
    public Session generate() {
        try {
            if (session == null) {
                SessionFactory factory = SessionFactoryImpl.newInstance();
                Map<String, String> parameter = new HashMap<>();
                parameter.put(SessionParameter.USER, credentials.getUser());
                parameter.put(SessionParameter.PASSWORD, credentials.getPassword());
                parameter.put(SessionParameter.BROWSER_URL, credentials.getServiceUrl());
                parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());

                List<Repository> repositories = factory.getRepositories(parameter);

                this.session = repositories.get(0).createSession();
            }
        }
        catch (CmisObjectNotFoundException e) {
            logger.error("Incorrect serviceUrl, please change it and restart the app (" + credentials.getServiceUrl() + ")");
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
}
