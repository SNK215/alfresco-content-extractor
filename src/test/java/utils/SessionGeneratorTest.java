package utils;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisUnauthorizedException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import fr.amexio.extractor.model.Credentials;
import fr.amexio.extractor.utils.SessionGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

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
    @DisplayName("Given correct parameters, when generate session, then returns session")
    public void givenCorrectParameters_whenGenerate_thenReturnsSession() throws IOException {
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
    @DisplayName("Given incorrect serviceUrl, when generate session, then throws CmisObjectNotFoundException")
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
    @DisplayName("Given incorrect user, when generate session, then throws CmisUnauthorizedException")
    @Disabled
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
    @DisplayName("Given incorrect password, when generate session, then throws CmisUnauthorizedException")
    @Disabled
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
    @DisplayName("Given already existing session, when generate session, then returns session")
    @Disabled
    public void givenAlreadyExistingSession_whenGenerate_thenReturnsSession() throws IOException {
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
