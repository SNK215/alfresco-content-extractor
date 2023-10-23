package model;

import org.example.model.Credentials;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CredentialsTest {

    @Mock
    File file;

    @Test
    @Disabled
    public void whenCredentialsCreated_shouldGetDataFromPropertyFile() {

        when(file.getAbsolutePath()).thenReturn("C:\\Users\\llangowski\\Documents\\TUTORIAL_HOME\\Projet Stage\\alfresco-content-extractor\\src\\test\\java\\ressources\\extractor_application.properties");

        Credentials credentials = new Credentials();

        assertThat(credentials.getServiceUrl()).isEqualTo("serviceUrlTest");
        assertThat(credentials.getUser()).isEqualTo("adminTest");
        assertThat(credentials.getPassword()).isEqualTo("adminTest");
        assertThat(credentials.getDestinationDirectory()).isEqualTo("C:/pathTest");
    }
}
