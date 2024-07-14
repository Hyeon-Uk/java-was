package codesquad.was.http.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidRequestFormatExceptionTest {
    @Test
    public void testDefaultConstructor() {
        // given
        InvalidRequestFormatException exception = new InvalidRequestFormatException();

        // when
        String message = exception.getMessage();

        // then
        assertEquals("Invalid request format", message);
    }
}