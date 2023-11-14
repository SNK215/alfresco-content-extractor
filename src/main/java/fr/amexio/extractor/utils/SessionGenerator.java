package fr.amexio.extractor.utils;

import fr.amexio.extractor.model.Credentials;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisConnectionException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisUnauthorizedException;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fr.amexio.extractor.utils.IHM.connectionTryCounter;

/**
 * Alfresco session generator
 *
 * @author Jeff Pott
 */
@Getter
@Setter
@Log4j2
@SuppressWarnings({"unused"})
public class SessionGenerator {

    private Session session = null;

    public SessionGenerator() {

    }

    /**
     * Takes data from a credentials object to create a new Alfresco session
     * @param credentials (credentials class takes data from extractor_application.properties)
     * @return Alfresco session
     */
    public Session generate(Credentials credentials) throws IOException {
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
            log.error("Incorrect serviceUrl, please change it and restart the app (" + credentials.getServiceUrl() + ")");
            new IHM().endProgram();
        }
        catch (CmisUnauthorizedException e) {
            connectionTryCounter--;
            log.error("Username or password is incorrect");
            new IHM().credentialsRequest();
        }
        catch(CmisConnectionException e) {
            log.error("Cannot connect to Alfresco, check if an Alfresco instance us running and try again");
            new IHM().endProgram();
        }
        return this.session;
    }
}
