package utils;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.example.utils.DirectoryManager;
import org.example.utils.SessionGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JsonCreatorTest {

    @Mock
    private SessionGenerator sessionGenerator;

    @BeforeEach
    public void setUp() {
        sessionGenerator = new SessionGenerator();
        Configurator.setLevel(LogManager.getLogger(DirectoryManager.class).getName(), Level.OFF);

    }


}
