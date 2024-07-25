package codesquad.was.http.message.vo;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpHeaderTest {

    @Test
    public void testDefaultConstructor() {
        HttpHeader httpHeader = new HttpHeader();
        assertTrue(httpHeader.allHeaders().isEmpty());
    }

    @Test
    public void testConstructorWithMap() {
        Map<String, List<String>> initialHeaders = Map.of(
                "Content-Type", List.of("text/html"),
                "Accept", List.of("text/html", "application/json")
        );
        HttpHeader httpHeader = new HttpHeader(initialHeaders);
        assertEquals(initialHeaders, httpHeader.allHeaders());
    }

    @Test
    public void testGetHeaders() {
        HttpHeader httpHeader = new HttpHeader();
        httpHeader.setHeader("Content-Type", "text/html");
        List<String> headers = httpHeader.getHeaders("Content-Type");
        assertEquals(1, headers.size());
        assertEquals("text/html", headers.get(0));
    }

    @Test
    public void testGetHeadersNonExisting() {
        HttpHeader httpHeader = new HttpHeader();
        List<String> headers = httpHeader.getHeaders("Non-Existing");
        assertTrue(headers.isEmpty());
    }

    @Test
    public void testAllHeaders() {
        HttpHeader httpHeader = new HttpHeader();
        httpHeader.setHeader("Content-Type", "text/html");
        Map<String, List<String>> headers = httpHeader.allHeaders();
        assertEquals(1, headers.size());
        assertEquals(List.of("text/html"), headers.get("Content-Type"));
    }

    @Test
    public void testSetHeader() {
        HttpHeader httpHeader = new HttpHeader();
        httpHeader.setHeader("Content-Type", "text/html, application/json");
        List<String> headers = httpHeader.getHeaders("Content-Type");
        assertEquals(2, headers.size());
        assertEquals("text/html", headers.get(0));
        assertEquals("application/json", headers.get(1));
    }

    @Test
    public void testAddHeader() {
        HttpHeader httpHeader = new HttpHeader();
        httpHeader.addHeader("Accept", "text/html");
        httpHeader.addHeader("Accept", "application/json");
        List<String> headers = httpHeader.getHeaders("Accept");
        assertEquals(2, headers.size());
        assertEquals("text/html", headers.get(0));
        assertEquals("application/json", headers.get(1));
    }

    @Test
    public void testAddHeaderToNewKey() {
        HttpHeader httpHeader = new HttpHeader();
        httpHeader.addHeader("Accept", "text/html");
        List<String> headers = httpHeader.getHeaders("Accept");
        assertEquals(1, headers.size());
        assertEquals("text/html", headers.get(0));
    }

    @Test
    public void testSetHeaderWithBlankValue(){
        HttpHeader httpHeader = new HttpHeader();
        httpHeader.setHeader("Accept", "text/html, ,application/json");
        List<String> headers = httpHeader.getHeaders("Accept");
        assertEquals(2, headers.size());
        assertEquals("text/html", headers.get(0));
        assertEquals("application/json", headers.get(1));
    }
}
