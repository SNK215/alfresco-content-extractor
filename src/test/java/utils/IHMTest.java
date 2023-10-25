package utils;

import org.example.utils.IHM;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.assertj.core.api.Assertions.*;

public class IHMTest {

    private IHM ihm = new IHM();

    @ParameterizedTest(name = "{0} must provoke System.exit()")
    @ValueSource(strings = {"x", "n", "1", "?"})
    @DisplayName("If user presses any other key when getUserChoice() then System.exit()")
    public void given_whenGetUserChoice_thenSystemExit(String args) throws Exception {
        //ARRANGE
        InputStream in = new ByteArrayInputStream(args.getBytes());

        //ACT
        System.setIn(in);
        int statusCode = catchSystemExit(() -> {
            ihm.getUserChoice();
        });

        //ASSERT
        assertThat(statusCode).isEqualTo(0);
    }

    @Test
    @DisplayName("Given insufficient disk space, when startPermission, then System.exit()")
    public void givenInsufficientDiskSpace_whenStartPermission_thenSystemExit() throws Exception {
        //ARRANGE
        long extractionSize = 12000;
        double convertExtractionSize = 12;
        String extractionSizePrefixMultiplier = "Mb";
        long availableDiskSpace = 11000;
        double convertAvailableDiskSpace = 11;
        String availableDiskSpacePrefixMultiplier = "Mb";

        //ACT
        int statusCode = catchSystemExit(() -> {
            ihm.startPermission(extractionSize, convertExtractionSize, extractionSizePrefixMultiplier, availableDiskSpace, convertAvailableDiskSpace, availableDiskSpacePrefixMultiplier);
        });

        //ASSERT
        assertThat(statusCode).isEqualTo(0);
    }
}










