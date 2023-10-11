import org.example.utils.DirectoryManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryManagerTest {
    private DirectoryManager directoryManager;
    private String user;
    private String destinationFolder;
    private String destinationFile;
    Path dirPath;


    @BeforeEach
    public void setUp() {
        directoryManager = new DirectoryManager();
        user = System.getProperty("user.name");
        destinationFolder = "C:\\Users\\" + user + "\\Desktop\\testDirectory";
        destinationFile = "C:\\Users\\" + user + "\\Desktop\\testDirectory\\testFile.txt";
        dirPath = Paths.get(destinationFolder);
    }

    @Test
    public void whenGivenNotExistingDirectory_thenCreateIt() throws IOException {

        directoryManager.prepareDestinationDirectory(destinationFolder);

        Assertions.assertTrue(Files.exists(dirPath));

        Files.delete(dirPath);
    }

    @Test
    public void whenGivenExistingFilledDirectory_thenCleanIt() throws IOException {
        File testDirectoryWithFile = new File(destinationFolder);
        testDirectoryWithFile.mkdir();

        for (int i = 0; i < 10; i++) {
            File testFile = new File(destinationFile+i);
            testFile.createNewFile();
        }

        directoryManager.prepareDestinationDirectory(destinationFolder);

        //Retuns false if the directory is empty
        boolean isEmptyDirectory = Files.list(dirPath).findAny().isPresent();

        Assertions.assertFalse(isEmptyDirectory);

        Files.delete(dirPath);
    }
}
