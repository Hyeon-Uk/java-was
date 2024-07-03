package codesquad.message.request;

import codesquad.http.message.InvalidRequestFormatException;
import codesquad.http.message.request.HttpMethod;
import codesquad.http.message.request.HttpRequestMessage;
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

    @Test
    public void queryStringMessage(){
        //given
        String queryStringMessage = """
            GET /create?username=hyeonuk&nickname=khu147&password=password1234 HTTP/1.1
            Host: localhost:8080
            User-Agent: Mozilla/5.0
            Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json
            Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8
            Accept-Encoding: gzip, deflate, sdch
            Connection: keep-alive
            Upgrade-Insecure-Requests: 1
            Content-Type: application/x-www-form-urlencoded
            
            """;
        //when
        HttpRequestMessage req = new HttpRequestMessage(queryStringMessage);

        //then
        assertAll("queryStringValidation",
                ()->assertEquals("hyeonuk",req.getQueryString("username")),
                ()->assertEquals("khu147",req.getQueryString("nickname")),
                ()->assertEquals("password1234",req.getQueryString("password"))
                );
    }
    @Test
    public void emptyQueryStringMessage(){
        //given
        String queryStringMessage = """
            GET /create?username=&nickname= HTTP/1.1
            Host: localhost:8080
            User-Agent: Mozilla/5.0
            Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json
            Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8
            Accept-Encoding: gzip, deflate, sdch
            Connection: keep-alive
            Upgrade-Insecure-Requests: 1
            Content-Type: application/x-www-form-urlencoded
            
            """;
        //when
        HttpRequestMessage req = new HttpRequestMessage(queryStringMessage);

        //then
        assertAll("blank return blank string",
                ()->assertEquals("",req.getQueryString("username")),
                ()->assertEquals("",req.getQueryString("nickname")));
    }

    @Test
    public void nullQueryStringMessage(){
        //given
        String queryStringMessage = """
            GET /create HTTP/1.1
            Host: localhost:8080
            User-Agent: Mozilla/5.0
            Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json
            Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8
            Accept-Encoding: gzip, deflate, sdch
            Connection: keep-alive
            Upgrade-Insecure-Requests: 1
            Content-Type: application/x-www-form-urlencoded
            
            """;
        //when
        HttpRequestMessage req = new HttpRequestMessage(queryStringMessage);

        //then
        assertAll("not exists query parameter return null",
                ()->assertNull(req.getQueryString("notExists")));
    }

    @Test
    public void urlEncodingQueryString(){
        //given
        //안녕하세요 url 인코딩 = %ec%95%88%eb%85%95%ed%95%98%ec%84%b8%ec%9a%94
        String queryStringMessage = """
            GET /create?name=%ec%95%88%eb%85%95%ed%95%98%ec%84%b8%ec%9a%94 HTTP/1.1
            Host: localhost:8080
            User-Agent: Mozilla/5.0
            Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json
            Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8
            Accept-Encoding: gzip, deflate, sdch
            Connection: keep-alive
            Upgrade-Insecure-Requests: 1
            Content-Type: application/x-www-form-urlencoded
            
            """;
        //when
        HttpRequestMessage req = new HttpRequestMessage(queryStringMessage);

        //then
        assertEquals("안녕하세요",req.getQueryString("name"));
    }
}