import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.exceptions.CmisUnauthorizedException;
import org.example.exceptions.DirectoryNotFoundException;
import org.example.utils.DirectoryManager;
import org.example.utils.Extractor;
import org.example.utils.JsonObjectCreator;
import org.example.utils.SessionGenerator;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExtractorTest {

    @Test(expected = CmisUnauthorizedException.class)
    public void whenIncorrectServiceUrl_shouldRaiseException() throws IOException, DirectoryNotFoundException {
        SessionGenerator sessionGenerator = new SessionGenerator();
        DirectoryManager directoryManager = new DirectoryManager();
        JsonObjectCreator jsonObjectCreator = new JsonObjectCreator();
        Extractor extractor = new Extractor(directoryManager, jsonObjectCreator, sessionGenerator);

        directoryManager.clean(extractor.getDestinationFolder());
        File newDir = new File(extractor.getDestinationFolder() + extractor.getTargetPath());
        if (!newDir.exists()){
            newDir.mkdirs();
        }

        sessionGenerator.setUser("wrongUser");
        Session session = sessionGenerator.generate();
    }
}
