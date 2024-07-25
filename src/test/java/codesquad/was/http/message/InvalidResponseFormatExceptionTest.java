package codesquad.was.http.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvalidResponseFormatExceptionTest {

    @Test
    public void testDefaultConstructor() {
        // given
        InvalidResponseFormatException exception = new InvalidResponseFormatException();

        // when
        String message = exception.getMessage();

        // then
        assertEquals("Invalid resopnse format", message);
    }
}