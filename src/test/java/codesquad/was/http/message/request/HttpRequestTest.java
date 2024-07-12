package codesquad.was.http.message.request;

import codesquad.message.mock.MockTimer;
import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.message.vo.HttpHeader;
import codesquad.was.http.message.vo.HttpRequestStartLine;
import codesquad.was.http.session.Session;
import codesquad.was.http.session.SessionManager;
import codesquad.was.http.session.SessionStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {
    private MockTimer timer;
    private HttpRequest request;
    private SessionManager sessionManager;
    private HttpRequestStartLine startLine;
    private HttpHeader header;
    private HttpBody body;
    private Map<String,String> queryString;
    @BeforeEach
    void setUp(){
        queryString = new HashMap<>();
        timer = new MockTimer(100l);
        sessionManager = new SessionManager(new SessionStorage(),timer);
        startLine = new HttpRequestStartLine("HTTP/1.1","/",HttpMethod.GET);
        header = new HttpHeader();
        body = new HttpBody();
        request = new HttpRequest(startLine,queryString,header,body,sessionManager);
    }

    @Test
    void getSessionWithoutCreateParam(){
        //given

        //when
        Session session = request.getSession();

        //then
        assertNotNull(session);
    }

    @Test
    void getSessionWithCreateParamTrue(){
        //given

        //when
        Session session = request.getSession(true);

        //then
        assertNotNull(session);
    }

    @Test
    void getSessionWithCreateParamFalse(){
        //given

        //when
        Session session = request.getSession(false);

        //then
        assertNull(session);
    }

    @Test
    void queryStringTest(){
        //given
        queryString.put("hello","world");

        //when
        String value = request.getQueryString("hello");

        //then
        assertEquals("world",value);
    }

    @Test
    void getUri(){
        //given

        //when
        String uri = request.getUri();

        //then
        assertEquals("/",uri);
    }
}