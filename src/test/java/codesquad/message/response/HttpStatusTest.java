package codesquad.message.response;

import static org.junit.jupiter.api.Assertions.*;

import codesquad.http.message.response.HttpStatus;
import org.junit.jupiter.api.Test;

public class HttpStatusTest {

    @Test
    public void testOK() {
        HttpStatus status = HttpStatus.OK;
        assertEquals(200, status.getCode());
        assertEquals("OK", status.getMessage());
    }

    @Test
    public void testCREATE() {
        HttpStatus status = HttpStatus.CREATE;
        assertEquals(201, status.getCode());
        assertEquals("Created", status.getMessage());
    }

    @Test
    public void testACCEPTED() {
        HttpStatus status = HttpStatus.ACCEPTED;
        assertEquals(202, status.getCode());
        assertEquals("Accepted", status.getMessage());
    }

    @Test
    public void testNO_CONTENT() {
        HttpStatus status = HttpStatus.NO_CONTENT;
        assertEquals(204, status.getCode());
        assertEquals("No Content", status.getMessage());
    }

    @Test
    public void testMOVED_PERMANENTLY() {
        HttpStatus status = HttpStatus.MOVED_PERMANENTLY;
        assertEquals(301, status.getCode());
        assertEquals("Moved Permanently", status.getMessage());
    }

    @Test
    public void testNOT_MODIFIED() {
        HttpStatus status = HttpStatus.NOT_MODIFIED;
        assertEquals(304, status.getCode());
        assertEquals("Not Modified", status.getMessage());
    }

    @Test
    public void testBAD_REQUEST() {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        assertEquals(400, status.getCode());
        assertEquals("Bad Request", status.getMessage());
    }

    @Test
    public void testUNAUTHORIZED() {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        assertEquals(401, status.getCode());
        assertEquals("Unauthorized", status.getMessage());
    }

    @Test
    public void testFORBIDDEN() {
        HttpStatus status = HttpStatus.FORBIDDEN;
        assertEquals(403, status.getCode());
        assertEquals("Forbidden", status.getMessage());
    }

    @Test
    public void testNOT_FOUND() {
        HttpStatus status = HttpStatus.NOT_FOUND;
        assertEquals(404, status.getCode());
        assertEquals("Not Found", status.getMessage());
    }

    @Test
    public void testMETHOD_NOT_ALLOWED() {
        HttpStatus status = HttpStatus.METHOD_NOT_ALLOWED;
        assertEquals(405, status.getCode());
        assertEquals("Method Not Allowed", status.getMessage());
    }

    @Test
    public void testINTERNAL_SERVER_ERROR() {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        assertEquals(500, status.getCode());
        assertEquals("Internal Server Error", status.getMessage());
    }

    @Test
    public void testBAD_GATEWAY() {
        HttpStatus status = HttpStatus.BAD_GATEWAY;
        assertEquals(502, status.getCode());
        assertEquals("Bad Gateway", status.getMessage());
    }

    @Test
    public void testGATEWAY_TIMEOUT() {
        HttpStatus status = HttpStatus.GATEWAY_TIMEOUT;
        assertEquals(504, status.getCode());
        assertEquals("Gateway Timeout", status.getMessage());
    }

    @Test
    public void testHTTP_VERSION_NOT_SUPPORTED() {
        HttpStatus status = HttpStatus.HTTP_VERSION_NOT_SUPPORTED;
        assertEquals(505, status.getCode());
        assertEquals("HTTP Version Not Supported", status.getMessage());
    }
}
