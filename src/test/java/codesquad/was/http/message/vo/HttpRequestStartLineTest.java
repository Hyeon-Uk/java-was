package codesquad.was.http.message.vo;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.vo.HttpRequestStartLine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class HttpRequestStartLineTest {

    @Test
    public void testConstructorAndGetters() {
        // given
        String httpVersion = "HTTP/1.1";
        String uri = "/example";
        HttpMethod method = HttpMethod.GET;

        // when
        HttpRequestStartLine startLine = new HttpRequestStartLine(httpVersion, uri, method);

        // then
        assertEquals(httpVersion, startLine.getHttpVersion());
        assertEquals(uri, startLine.getUri());
        assertEquals(method, startLine.getMethod());
    }

    @Test
    public void testHttpVersion() {
        // given
        String httpVersion = "HTTP/2.0";
        String uri = "/test";
        HttpMethod method = HttpMethod.POST;

        // when
        HttpRequestStartLine startLine = new HttpRequestStartLine(httpVersion, uri, method);

        // then
        assertEquals(httpVersion, startLine.getHttpVersion());
    }

    @Test
    public void testUri() {
        // given
        String httpVersion = "HTTP/1.1";
        String uri = "/anotherTest";
        HttpMethod method = HttpMethod.PUT;

        // when
        HttpRequestStartLine startLine = new HttpRequestStartLine(httpVersion, uri, method);

        // then
        assertEquals(uri, startLine.getUri());
    }

    @Test
    public void testMethod() {
        // given
        String httpVersion = "HTTP/1.0";
        String uri = "/";
        HttpMethod method = HttpMethod.DELETE;

        // when
        HttpRequestStartLine startLine = new HttpRequestStartLine(httpVersion, uri, method);

        // then
        assertEquals(method, startLine.getMethod());
    }
}
