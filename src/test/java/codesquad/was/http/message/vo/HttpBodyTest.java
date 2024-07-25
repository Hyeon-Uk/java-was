package codesquad.was.http.message.vo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class HttpBodyTest {

    @Test
    public void testConstructorWithString() {
        String bodyString = "Hello, World!";
        HttpBody httpBody = new HttpBody(bodyString);
        assertArrayEquals(bodyString.getBytes(), httpBody.getBody());
    }

    @Test
    public void testConstructorWithByteArray() {
        byte[] bodyBytes = "Hello, World!".getBytes();
        HttpBody httpBody = new HttpBody(bodyBytes);
        assertArrayEquals(bodyBytes, httpBody.getBody());
    }

    @Test
    public void testDefaultConstructor() {
        HttpBody httpBody = new HttpBody();
        assertArrayEquals(new byte[0], httpBody.getBody());
    }

    @Test
    public void testSetBodyWithByteArray() {
        byte[] bodyBytes = "Hello, World!".getBytes();
        HttpBody httpBody = new HttpBody();
        httpBody.setBody(bodyBytes);
        assertArrayEquals(bodyBytes, httpBody.getBody());
    }

    @Test
    public void testSetBodyWithString() {
        String bodyString = "Hello, World!";
        HttpBody httpBody = new HttpBody();
        httpBody.setBody(bodyString);
        assertArrayEquals(bodyString.getBytes(), httpBody.getBody());
    }

    @Test
    public void testGetBody() {
        byte[] bodyBytes = "Hello, World!".getBytes();
        HttpBody httpBody = new HttpBody(bodyBytes);
        assertArrayEquals(bodyBytes, httpBody.getBody());
    }
}

