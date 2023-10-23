package utils;

import org.example.utils.IHM;
import org.example.utils.SizeCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.github.stefanbirkner.systemlambda.SystemLambda.catchSystemExit;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.verify;

public class IHMTest {

    private IHM ihm = new IHM();

    @Mock
    SizeCalculator sizeCalculator;


    @Test
    @DisplayName("If user presses Y when getUserChoice() then returns Y")
    public void givenY_whenGetUserChoice_thenReturnY() {
        //ARRANGE
        String input = "y";
        InputStream in = new ByteArrayInputStream(input.getBytes());

        //ACT
        System.setIn(in);

        //ASSERTs
        assertThat(ihm.getUserChoice()).isEqualTo("y");
    }

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

}










