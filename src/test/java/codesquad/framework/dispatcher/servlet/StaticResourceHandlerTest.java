package codesquad.framework.dispatcher.servlet;

import codesquad.framework.mock.MockFileDatabase;
import codesquad.framework.mock.MockFileUtil;
import codesquad.was.http.exception.HttpNotFoundException;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.message.vo.HttpHeader;
import codesquad.was.http.message.vo.HttpRequestStartLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class StaticResourceHandlerTest {
    private StaticResourceHandler staticHandler;
    private MockFileUtil fileUtil;
    private MockFileDatabase fileDatabase;

    @BeforeEach
    public void setUp() {
        fileUtil = new MockFileUtil();
        fileDatabase = new MockFileDatabase();
        staticHandler = new StaticResourceHandler(fileUtil, fileDatabase);
    }

    class MockHttpRequest extends HttpRequest {

        public MockHttpRequest(String path) {
            super(new HttpRequestStartLine("version", path, HttpMethod.GET),
                    new HashMap<>(), new HttpHeader(new HashMap<>()), new HttpBody(), null);
        }
    }

    @Test
    public void testHandleStaticContent() {
        HttpRequest request = new MockHttpRequest("/test.html");
        HttpResponse response = new HttpResponse("HTTP/1.1", new HashMap<>());

        staticHandler.handle(request, response);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("text/html", response.getHeaders("Content-Type").get(0));
        assertArrayEquals("<html><body><h1>Test Content</h1></body></html>".getBytes(), response.getBody());
    }

    @Test
    public void testHandleImageContent() {
        HttpRequest request = new MockHttpRequest("/images/image.png");
        HttpResponse response = new HttpResponse("HTTP/1.1", new HashMap<>());

        staticHandler.handle(request, response);

        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals("image/png", response.getHeaders("Content-Type").get(0));
        assertArrayEquals(new byte[]{0x39, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A}, response.getBody());
    }

    @Test
    public void testHandleUnknownContent() {
        HttpRequest request = new MockHttpRequest("/unknown/test.txt");
        HttpResponse response = new HttpResponse("HTTP/1.1", new HashMap<>());

        assertThrows(HttpNotFoundException.class,()->{
            staticHandler.handle(request, response);
        });
    }
}