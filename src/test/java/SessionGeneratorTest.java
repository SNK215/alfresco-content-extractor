import org.example.utils.DirectoryManager;
import org.example.utils.SessionGenerator;
import org.junit.jupiter.api.BeforeEach;

import java.nio.file.Paths;

public class SessionGeneratorTest {

    private SessionGenerator sessionGenerator;

    @BeforeEach
    public void setUp() {
        sessionGenerator = new SessionGenerator();
    }
}
