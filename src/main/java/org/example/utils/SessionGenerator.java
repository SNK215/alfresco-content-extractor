package org.example.utils;

import lombok.Getter;
import lombok.Setter;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisUnauthorizedException;
import org.apache.logging.log4j.*;
import org.example.model.Credentials;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@SuppressWarnings({"unused"})
public class SessionGenerator {
    private Session session = null;
    private static final Logger logger = LogManager.getLogger();

    public SessionGenerator() {

    }

    public Session generate(Credentials credentials) {
        try {
            if (session == null) {
                SessionFactory factory = SessionFactoryImpl.newInstance();
                Map<String, String> parameter = new HashMap<>();
                String user = credentials.getUser();
                String password = credentials.getPassword();
                String serviceUrl = credentials.getServiceUrl();
                parameter.put(SessionParameter.USER, user);
                parameter.put(SessionParameter.PASSWORD, password);
                parameter.put(SessionParameter.BROWSER_URL, serviceUrl);
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
}
