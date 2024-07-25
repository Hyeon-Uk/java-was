package codesquad.was.http.message.request;

import codesquad.message.mock.MockTimer;
import codesquad.was.http.cookie.Cookie;
import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.message.vo.HttpHeader;
import codesquad.was.http.message.vo.HttpRequestStartLine;
import codesquad.was.http.session.Session;
import codesquad.was.http.session.SessionManager;
import codesquad.was.http.session.SessionStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {
    private MockTimer timer;
    private HttpRequest request;
    private SessionManager sessionManager;
    private HttpRequestStartLine startLine;
    private HttpHeader header;
    private HttpBody body;
    private Map<String, String> queryString;

    @BeforeEach
    void setUp() {
        queryString = new HashMap<>();
        timer = new MockTimer(100l);
        sessionManager = new SessionManager(new SessionStorage(), timer);
        startLine = new HttpRequestStartLine("HTTP/1.1", "/", HttpMethod.GET);
        header = new HttpHeader();
        body = new HttpBody();
        request = new HttpRequest(startLine, queryString, header, body, sessionManager);
    }

    @Test
    void getSessionWithoutCreateParam() {
        //given

        //when
        Session session = request.getSession();

        //then
        assertNotNull(session);
    }

    @Test
    void getSessionWithCreateParamTrue() {
        //given

        //when
        Session session = request.getSession(true);

        //then
        assertNotNull(session);
    }

    @Test
    void getSessionWithCreateParamFalse() {
        //given

        //when
        Session session = request.getSession(false);

        //then
        assertNull(session);
    }

    @Test
    void queryStringTest() {
        //given
        queryString.put("hello", "world");

        //when
        String value = request.getQueryString("hello");

        //then
        assertEquals("world", value);
    }

    @Test
    void getUri() {
        //given

        //when
        String uri = request.getUri();

        //then
        assertEquals("/", uri);
    }

    @Test
    void getBody() {
        //given
        byte[] bytes = "hello world".getBytes();
        body.setBody(bytes);

        //when
        byte[] bodyBytes = request.getBody();

        //then
        assertEquals(bytes, bodyBytes);
    }

    @Test
    void getCookies() {
        //given
        header.setHeader("Cookie", "name1=value1;name2=value2");

        //when
        List<Cookie> cookies = request.getCookies();

        //then
        assertTrue(cookies.stream().anyMatch(c -> c.getName().equals("name1") && c.getValue().equals("value1")));
        assertTrue(cookies.stream().anyMatch(c -> c.getName().equals("name2") && c.getValue().equals("value2")));
    }

    @Test
    void isNewSessionTestWithFalse() {
        //given

        //when & then
        assertTrue(request.isNewSession());
    }

    @Test
    void isNewSessionTestWithTrue() {
        //given
        header.setHeader("Cookie", "SID=blah");

        //when & then
        assertFalse(request.isNewSession());
    }

    @Test
    void getMethodTest() {
        //given

        //when & then
        assertEquals(HttpMethod.GET, request.getMethod());
    }

    @Test
    void getHttpVersion() {
        assertEquals("HTTP/1.1", request.getHttpVersion());
    }

    @Test
    void getHeader() {
        //given
        header.setHeader("key", "value1,value2");

        //when
        List<String> headers = request.getHeader("key");

        //then
        assertTrue(headers.contains("value1"));
        assertTrue(headers.contains("value2"));
    }
}