package codesquad.message.request;

import codesquad.message.InvalidRequestFormatException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestMessageTest {
    private String testGetMessage = """
            GET /index.html HTTP/1.1
            Host: localhost:8080
            User-Agent: Mozilla/5.0
            Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json
            Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8
            Accept-Encoding: gzip, deflate, sdch
            Connection: keep-alive
            Upgrade-Insecure-Requests: 1
            Content-Type: application/x-www-form-urlencoded
            
            """;

    private String wrongMessage = """
            Unknown uri
            Host: unknown
            """;

    @Test
    public void parseGetMessage() throws InvalidRequestFormatException {
        //given
        HttpRequestMessage req = new HttpRequestMessage(testGetMessage);

        //when & then
        assertAll("request message validation",
                ()->assertEquals(HttpMethod.GET,req.getMethod()),
                ()->assertEquals("/index.html",req.getUri()),
                ()->assertEquals("HTTP/1.1",req.getHttpVersion()),
                ()->assertEquals("localhost:8080",req.getHost()),
                ()->assertEquals("Mozilla/5.0",req.getHeader("User-Agent")),
                ()->assertEquals("text/html,application/xhtml+xml,application/xml;q=0.9,application/json",req.getHeader("Accept")),
                ()->assertEquals("ko-KR,ko;q=0.9,zh-CN,zh;q=0.8",req.getHeader("Accept-Language")),
                ()->assertEquals("gzip, deflate, sdch",req.getHeader("Accept-Encoding")),
                ()->assertEquals("keep-alive",req.getHeader("Connection")),
                ()->assertEquals("1",req.getHeader("Upgrade-Insecure-Requests")),
                ()->assertEquals("application/x-www-form-urlencoded",req.getHeader("Content-Type"))
        );
    }

    @Test
    public void parseWrongMessage(){
        assertThrows(InvalidRequestFormatException.class,()->{
            HttpRequestMessage req = new HttpRequestMessage(wrongMessage);
        });
    }

}