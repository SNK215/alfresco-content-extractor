package utils;

import org.example.utils.Extractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ExtractorTest {

    private final Extractor extractor = new Extractor("");

    @Test
    public void givenMillisecondsTime_whenAddAttributesToFile_shouldAddCorrectValues() throws IOException {
        //ARRANGE
        File file = new File("../testFile.txt");
        file.mkdir();
        long creationDateMs = 1698138624477L;
        long lastModifDateMs = 1698138624888L;

        //ACT
        extractor.addAttributesToFile(file, creationDateMs, lastModifDateMs);

        FileTime filetimeCreation = (FileTime) Files.getAttribute(file.toPath(), "creationTime");
        long lastModifTime = file.lastModified();

        //ASSERT
        assertThat(filetimeCreation.toString()).isEqualTo("2023-10-24T09:10:24.477Z");
        assertThat(lastModifTime).isEqualTo(lastModifDateMs);

        file.delete();
    }
}
