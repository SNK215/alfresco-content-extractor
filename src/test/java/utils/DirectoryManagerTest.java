package utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.example.utils.DirectoryManager;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class DirectoryManagerTest {
    private DirectoryManager directoryManager;
    private String user;
    private String destinationDirectory;
    private String destinationFile;
    Path dirPath;


    @BeforeEach
    public void setUp() {
        Configurator.setLevel(LogManager.getLogger(DirectoryManager.class).getName(), Level.OFF);
        directoryManager = new DirectoryManager();
        user = System.getProperty("user.name");
        destinationDirectory = "C:\\Users\\" + user + "\\Desktop\\testDirectory";
        destinationFile = "C:\\Users\\" + user + "\\Desktop\\testDirectory\\testFile.txt";
        dirPath = Paths.get(destinationDirectory);
    }

    @Test
    @DisplayName("When destination directory doesn't exist, then create it")
    public void whenGivenNotExistingDirectory_thenCreateIt() throws IOException {
        //ACT
        directoryManager.prepareDestinationDirectory(destinationDirectory);

        //ASSERT
        assertThat(Files.exists(dirPath)).isTrue();

        Files.delete(dirPath);

    }

    @Test
    @DisplayName("When destination directory exists, then delete its content")
    public void whenGivenExistingFilledDirectory_thenCleanIt() throws IOException {
        //ARRANGE
        File testDirectoryWithFile = new File(destinationDirectory);
        testDirectoryWithFile.mkdir();

        for (int i = 0; i < 10; i++) {
            File testFile = new File(destinationFile+i);
            testFile.createNewFile();
        }

        //ACT
        directoryManager.prepareDestinationDirectory(destinationDirectory);

        //ASSERT
        //Retuns false if the directory is empty
        boolean isEmptyDirectory = Files.list(dirPath).findAny().isPresent();

        assertThat(isEmptyDirectory).isFalse();

        Files.delete(dirPath);
    }

    @Test
    @DisplayName("When incorrect destination directory, then throws exception")
    public void whenGivenIncorrectDestinationDirectory_thenThrowsException() throws IOException {
        //ARRANGE
        destinationDirectory = "incorrectDestinationDirectory?/";

        //ACT
        //ASSERT
        assertThatThrownBy(()->directoryManager.prepareDestinationDirectory(destinationDirectory)).isInstanceOf(Exception.class);
    }
}
