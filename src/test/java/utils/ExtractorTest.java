package utils;

import org.example.utils.Extractor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
public class ExtractorTest {

    private final Extractor extractor = new Extractor("");

    @Test
    @DisplayName("Given time in milliseconds, when addAttributesToFile, then should add correct values")
    public void givenMillisecondsTime_whenAddAttributesToFile_shouldAddCorrectValues() throws IOException {
        //ARRANGE
        File folder = new File("../testFolder");
        folder.mkdir();
        long creationDateMs = 1698138624477L;
        long lastModifDateMs = 1698138624888L;

        //ACT
        extractor.addAttributesToFile(folder, creationDateMs, lastModifDateMs);

        FileTime filetimeCreation = (FileTime) Files.getAttribute(folder.toPath(), "creationTime");
        long lastModifTime = folder.lastModified();

        //ASSERT
        assertThat(filetimeCreation.toString()).isEqualTo("2023-10-24T09:10:24.477Z");
        assertThat(lastModifTime).isEqualTo(lastModifDateMs);

        folder.delete();
    }
}
