package codesquad.message.request;

import codesquad.http.message.request.HttpMethod;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class HttpMethodTest {
    @Test
    public void fromGet(){
        assertEquals(HttpMethod.GET, HttpMethod.from("GET"));
        assertEquals(HttpMethod.GET, HttpMethod.from("get"));
        assertEquals(HttpMethod.GET, HttpMethod.from("Get"));
    }

    @Test
    public void fromPost(){
        assertEquals(HttpMethod.POST, HttpMethod.from("POST"));
        assertEquals(HttpMethod.POST, HttpMethod.from("Post"));
        assertEquals(HttpMethod.POST, HttpMethod.from("post"));
    }

    @Test
    public void fromDelete(){
        assertEquals(HttpMethod.DELETE, HttpMethod.from("DELETE"));
        assertEquals(HttpMethod.DELETE, HttpMethod.from("Delete"));
        assertEquals(HttpMethod.DELETE, HttpMethod.from("delete"));
    }

    @Test
    public void fromPatch(){
        assertEquals(HttpMethod.PATCH, HttpMethod.from("PATCH"));
        assertEquals(HttpMethod.PATCH, HttpMethod.from("Patch"));
        assertEquals(HttpMethod.PATCH, HttpMethod.from("patch"));
    }

    @Test
    public void fromPut(){
        assertEquals(HttpMethod.PUT, HttpMethod.from("PUT"));
        assertEquals(HttpMethod.PUT, HttpMethod.from("Put"));
        assertEquals(HttpMethod.PUT, HttpMethod.from("put"));
    }

    @Test
    public void fromOptions(){
        assertEquals(HttpMethod.OPTIONS, HttpMethod.from("OPTIONS"));
        assertEquals(HttpMethod.OPTIONS, HttpMethod.from("Options"));
        assertEquals(HttpMethod.OPTIONS, HttpMethod.from("options"));
    }

    @Test
    public void fromHead(){
        assertEquals(HttpMethod.HEAD, HttpMethod.from("HEAD"));
        assertEquals(HttpMethod.HEAD, HttpMethod.from("Head"));
        assertEquals(HttpMethod.HEAD, HttpMethod.from("head"));
    }

    @Test
    public void fromTrace(){
        assertEquals(HttpMethod.TRACE, HttpMethod.from("TRACE"));
        assertEquals(HttpMethod.TRACE, HttpMethod.from("Trace"));
        assertEquals(HttpMethod.TRACE, HttpMethod.from("trace"));
    }

    @Test
    public void fromConnect(){
        assertEquals(HttpMethod.CONNECT, HttpMethod.from("CONNECT"));
        assertEquals(HttpMethod.CONNECT, HttpMethod.from("Connect"));
        assertEquals(HttpMethod.CONNECT, HttpMethod.from("connect"));
    }

    @Test
    public void fromUnknown(){
        assertNull(HttpMethod.from("unknown"));
    }
}