package codesquad.message.request;

import codesquad.was.http.message.request.HttpMethod;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HttpMethodTest {

    @ParameterizedTest
    @ValueSource(strings = {"GET", "get", "Get"})
    public void fromGet(String method) {
        assertEquals(HttpMethod.GET, HttpMethod.from(method));
    }

    @ParameterizedTest
    @ValueSource(strings = {"POST", "Post", "post"})
    public void fromPost(String method) {
        assertEquals(HttpMethod.POST, HttpMethod.from(method));
    }

    @ParameterizedTest
    @ValueSource(strings = {"DELETE", "Delete", "delete"})
    public void fromDelete(String method) {
        assertEquals(HttpMethod.DELETE, HttpMethod.from(method));
    }

    @ParameterizedTest
    @ValueSource(strings = {"PATCH", "Patch", "patch"})
    public void fromPatch(String method) {
        assertEquals(HttpMethod.PATCH, HttpMethod.from(method));
    }

    @ParameterizedTest
    @ValueSource(strings = {"PUT", "Put", "put"})
    public void fromPut(String method) {
        assertEquals(HttpMethod.PUT, HttpMethod.from(method));
    }

    @ParameterizedTest
    @ValueSource(strings = {"OPTIONS", "Options", "options"})
    public void fromOptions(String method) {
        assertEquals(HttpMethod.OPTIONS, HttpMethod.from(method));
    }

    @ParameterizedTest
    @ValueSource(strings = {"HEAD", "Head", "head"})
    public void fromHead(String method) {
        assertEquals(HttpMethod.HEAD, HttpMethod.from(method));
    }

    @ParameterizedTest
    @ValueSource(strings = {"TRACE", "Trace", "trace"})
    public void fromTrace(String method) {
        assertEquals(HttpMethod.TRACE, HttpMethod.from(method));
    }

    @ParameterizedTest
    @ValueSource(strings = {"CONNECT", "Connect", "connect"})
    public void fromConnect(String method) {
        assertEquals(HttpMethod.CONNECT, HttpMethod.from(method));
    }

    @ParameterizedTest
    @ValueSource(strings = {"unknown", "", "InvalidMethod"})
    public void fromUnknown(String method) {
        assertNull(HttpMethod.from(method));
    }
}
