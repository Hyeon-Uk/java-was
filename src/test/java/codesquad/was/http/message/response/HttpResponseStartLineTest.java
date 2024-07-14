package codesquad.was.http.message.response;

import codesquad.was.http.message.InvalidResponseFormatException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class HttpResponseStartLineTest {

    @Test
    public void testHttpResponseStartLineWithValidArguments() {
        // Given valid arguments
        String httpVersion = "HTTP/1.1";
        HttpStatus status = HttpStatus.OK;

        // When creating HttpResponseStartLine
        HttpResponseStartLine startLine = new HttpResponseStartLine(httpVersion, status);

        // Then
        assertEquals(httpVersion, startLine.getHttpVersion());
        assertEquals(status, startLine.getStatus());
    }

    @Test
    public void testHttpResponseStartLineWithNullHttpVersion() {
        // Given null httpVersion
        String httpVersion = null;
        HttpStatus status = HttpStatus.NOT_FOUND;

        // When creating HttpResponseStartLine with null httpVersion
        assertThrows(InvalidResponseFormatException.class, () -> new HttpResponseStartLine(httpVersion, status));
    }

    @Test
    public void testHttpResponseStartLineWithNullStatus() {
        // Given null status
        String httpVersion = "HTTP/1.1";
        HttpStatus status = null;

        // When creating HttpResponseStartLine with null status
        assertThrows(InvalidResponseFormatException.class, () -> new HttpResponseStartLine(httpVersion, status));
    }

    @Test
    public void testHttpResponseStartLineWithValidHttpVersionOnly() {
        // Given valid httpVersion only
        String httpVersion = "HTTP/1.1";

        // When creating HttpResponseStartLine with only httpVersion
        HttpResponseStartLine startLine = new HttpResponseStartLine(httpVersion);

        // Then
        assertEquals(httpVersion, startLine.getHttpVersion());
        assertEquals(HttpStatus.OK, startLine.getStatus());
    }

    @Test
    public void testHttpResponseStartLineIsValidated() {
        // Given valid httpVersion and status
        String httpVersion = "HTTP/1.1";
        HttpStatus status = HttpStatus.OK;
        HttpResponseStartLine startLine = new HttpResponseStartLine(httpVersion, status);

        // Then
        assertTrue(startLine.isValidated());
    }

    @Test
    public void testHttpResponseStartLineHttpVersionNotValidated() throws NoSuchFieldException, IllegalAccessException {
        // Given null httpVersion and status
        String httpVersion = "HTTP/1.1";
        HttpStatus status = HttpStatus.OK;
        HttpResponseStartLine startLine = new HttpResponseStartLine(httpVersion, status);
        Field field = HttpResponseStartLine.class.getDeclaredField("httpVersion");
        field.setAccessible(true);
        field.set(startLine,null);

        // Then
        assertFalse(startLine.isValidated());
    }
    @Test
    public void testHttpResponseStartLineHttpStatusNotValidated() throws NoSuchFieldException, IllegalAccessException {
        // Given null httpVersion and status
        String httpVersion = "HTTP/1.1";
        HttpStatus status = HttpStatus.OK;
        HttpResponseStartLine startLine = new HttpResponseStartLine(httpVersion, status);
        Field field = HttpResponseStartLine.class.getDeclaredField("status");
        field.setAccessible(true);
        field.set(startLine,null);

        // Then
        assertFalse(startLine.isValidated());
    }

    @Test
    public void testParseStartLine() {
        // Given valid httpVersion and status
        String httpVersion = "HTTP/1.1";
        HttpStatus status = HttpStatus.OK;
        HttpResponseStartLine startLine = new HttpResponseStartLine(httpVersion, status);

        // When parsing start line
        byte[] parsedStartLine = startLine.parseStartLine();
        String parsedStartLineString = new String(parsedStartLine);

        // Then
        assertTrue(parsedStartLineString.startsWith("HTTP/1.1"));
        assertTrue(parsedStartLineString.contains("200 OK"));
    }
}
