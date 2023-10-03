import org.example.exceptions.DirectoryNotFoundException;
import org.example.utils.DirectoryManager;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryCleanerTest {

    private File folder;
    private File file;
    private String username;
    private DirectoryManager directoryManager;

    @BeforeEach
    public void setUp() throws IOException {
        username = System.getProperty("user.name");

        folder  = new File("C:\\Users\\"+username+"\\Desktop\\testFolder");
        folder.mkdir();

        file = new File("C:\\Users\\"+username+"\\Desktop\\testFolder\\test.txt");
        file.createNewFile();

        directoryManager = new DirectoryManager();
    }

    @Test
    public void givenDataFilledDirectory_whenClean_thenIsEmptyDirectoryShouldBeTrue() throws DirectoryNotFoundException, IOException {

        Path dirPath = Paths.get(folder.getPath());
        //https://howtodoinjava.com/java/io/check-empty-directory/
        //The Files.list(dirPath) returns a lazily populated Stream of files and directories (non-recursive)
        // in a given path. We can use the stream.findAny() method that returns an empty Optionalif the directoryis empty.
        boolean isEmptyDirectory = Files.list(dirPath).findAny().isPresent();

        directoryManager.clean("C:\\Users\\"+username+"\\Desktop\\testFolder");

        Assert.assertTrue(isEmptyDirectory);
    }

    @Test
    public void givenIncorrectDirectory_whenClean_shouldThrowDirectoryNotFoundException() throws DirectoryNotFoundException, IOException {

        Assertions.assertThrows(DirectoryNotFoundException.class, ()->
                directoryManager.clean("C:\\Users\\"+username+"\\Desktop\\incorrectFolder"));
    }


}
