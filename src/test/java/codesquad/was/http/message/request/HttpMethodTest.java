package codesquad.was.http.message.request;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class HttpMethodTest {
    @ParameterizedTest
    @CsvSource({
            "GET, GET",
            "POST, POST",
            "PUT, PUT",
            "DELETE, DELETE",
            "PATCH, PATCH",
            "HEAD, HEAD",
            "OPTIONS, OPTIONS",
            "TRACE, TRACE",
            "CONNECT, CONNECT"
    })
    public void testFromValidMethods(String input, String expected) {
        assertEquals(HttpMethod.valueOf(expected), HttpMethod.from(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"INVALID", "PUTT"})
    public void testFromInvalidMethod(String input) {
        assertNull(HttpMethod.from(input));
    }

    @ParameterizedTest
    @CsvSource({
            "GET, GET",
            "POST, POST",
            "PUT, PUT",
            "DELETE, DELETE",
            "PATCH, PATCH",
            "HEAD, HEAD",
            "OPTIONS, OPTIONS",
            "TRACE, TRACE",
            "CONNECT, CONNECT"
    })
    public void testMethodNames(String input, String expected) {
        assertEquals(expected, HttpMethod.valueOf(input).getMethod());
    }

    @ParameterizedTest
    @CsvSource({
            "GET, GET",
            "POST, POST",
            "PUT, PUT",
            "DELETE, DELETE",
            "PATCH, PATCH",
            "HEAD, HEAD",
            "OPTIONS, OPTIONS",
            "TRACE, TRACE",
            "CONNECT, CONNECT"
    })
    public void testToString(String input, String expected) {
        assertEquals(expected, HttpMethod.valueOf(input).toString());
    }
}