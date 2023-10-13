package utils;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisUnauthorizedException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.example.model.Credentials;
import org.example.utils.SessionGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class SessionGeneratorTest {

    private SessionGenerator sessionGenerator;
    @Mock
    Credentials credentials;

    @BeforeEach
    public void setUp() {
        Configurator.setLevel(LogManager.getLogger(SessionGenerator.class).getName(), Level.OFF);
        sessionGenerator = new SessionGenerator();
    }

    @Test
    public void givenCorrectParameters_whenGenerate_thenReturnsSession() {
        //ARRANGE
        when(credentials.getUser()).thenReturn("admin");
        when(credentials.getPassword()).thenReturn("admin");
        when(credentials.getServiceUrl()).thenReturn("http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser");

        //ACT
        Session session = sessionGenerator.generate(credentials);

        //ASSERT
        verify(credentials).getUser();
        verify(credentials).getPassword();
        verify(credentials).getServiceUrl();
        assertThat(session.getSessionParameters().get(SessionParameter.USER)).isEqualTo("admin");
        assertThat(session.getSessionParameters().get(SessionParameter.PASSWORD)).isEqualTo("admin");
        assertThat(session.getSessionParameters().get(SessionParameter.BROWSER_URL)).isEqualTo("http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser");
        assertThat(session.getSessionParameters().get(SessionParameter.BINDING_TYPE)).isEqualTo(BindingType.BROWSER.value());
    }

    @Test
    public void givenIncorrectServiceUrl_whenGenerate_thenThrowsCmisObjectNotFoundException() {
        //ARRANGE
        when(credentials.getUser()).thenReturn("admin");
        when(credentials.getPassword()).thenReturn("admin");
        when(credentials.getServiceUrl()).thenReturn("http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/brows");

        //ACT
        //ASSERT
        assertThatThrownBy(()->sessionGenerator.generate(credentials)).isInstanceOf(CmisObjectNotFoundException.class);
    }

    @Test
    public void givenIncorrectUser_whenGenerate_thenThrowsCmisUnauthorizedException() {
        //ARRANGE
        when(credentials.getUser()).thenReturn("incorrectUser");
        when(credentials.getPassword()).thenReturn("admin");
        when(credentials.getServiceUrl()).thenReturn("http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser");

        //ACT
        //ASSERT
        assertThatThrownBy(()->sessionGenerator.generate(credentials)).isInstanceOf(CmisUnauthorizedException.class);
    }

    @Test
    public void givenIncorrectPassword_whenGenerate_thenThrowsCmisUnauthorizedException() {
        //ARRANGE
        when(credentials.getUser()).thenReturn("admin");
        when(credentials.getPassword()).thenReturn("incorrectPassword");
        when(credentials.getServiceUrl()).thenReturn("http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser");

        //ACT
        //ASSERT
        assertThatThrownBy(()->sessionGenerator.generate(credentials)).isInstanceOf(CmisUnauthorizedException.class);
    }

    @Test
    public void givenAlreadyExistingSession_whenGenerate_thenReturnsSession() {
        //ARRANGE
        when(credentials.getUser()).thenReturn("admin");
        when(credentials.getPassword()).thenReturn("admin");
        when(credentials.getServiceUrl()).thenReturn("http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser");

        //ACT
        Session session = sessionGenerator.generate(credentials);
        sessionGenerator.setSession(session);

        //ASSERT
        verify(credentials).getUser();
        verify(credentials).getPassword();
        verify(credentials).getServiceUrl();
        assertThat(session).isEqualTo(sessionGenerator.generate(credentials));
        assertThat(session.getSessionParameters().get(SessionParameter.USER)).isEqualTo("admin");
        assertThat(session.getSessionParameters().get(SessionParameter.PASSWORD)).isEqualTo("admin");
        assertThat(session.getSessionParameters().get(SessionParameter.BROWSER_URL)).isEqualTo("http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser");
        assertThat(session.getSessionParameters().get(SessionParameter.BINDING_TYPE)).isEqualTo(BindingType.BROWSER.value());
    }
}
