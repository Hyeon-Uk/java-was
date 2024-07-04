package codesquad.http.message.parser;

import codesquad.http.message.InvalidRequestFormatException;
import codesquad.http.message.parser.*;
import codesquad.http.message.request.HttpMethod;
import codesquad.http.message.request.HttpRequestMessage;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestParsingTest {
    private String testGetMessage = withSystemLineSeparator("""
            GET /index.html HTTP/1.1
            Host: localhost:8080
            User-Agent: Mozilla/5.0
            Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json
            Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8
            Accept-Encoding: gzip, deflate, sdch
            Connection: keep-alive
            Upgrade-Insecure-Requests: 1
            Content-Type: application/x-www-form-urlencoded
            
            """);

    private String wrongMessage = withSystemLineSeparator("""
            Unknown uri
            Host: unknown
            """);

    private HttpRequestStartLineParser startLineParser = new HttpRequestStartLineParser();
    private HttpHeaderParser headerParser = new HttpHeaderParser();
    private HttpBodyParser bodyParser = new HttpBodyParser();
    private HttpQueryStringParser queryStringParser = new HttpQueryStringParser();
    private HttpRequestParser requestParser = new HttpRequestParser(startLineParser,headerParser,bodyParser,queryStringParser);

    private void verifyHeaderValues(HttpRequestMessage req,String key,String valueString){
        List<String> values = Arrays.stream(valueString.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();

        for(String value : values){
            assertTrue(req.getHeader(key).contains(value));
        }
    }
    @Test
    public void parseGetMessage() throws InvalidRequestFormatException {
        //given
        HttpRequestMessage req = requestParser.parse(testGetMessage);

        //when & then
        assertAll("request message validation",
                ()->assertEquals(HttpMethod.GET,req.getMethod()),
                ()->assertEquals("/index.html",req.getUri()),
                ()->assertEquals("HTTP/1.1",req.getHttpVersion()),
                ()->assertTrue(req.getHeader("Host").contains("localhost:8080")),
                ()->assertTrue(req.getHeader("User-Agent").contains("Mozilla/5.0")),
                ()->verifyHeaderValues(req,"Accept","text/html,application/xhtml+xml,application/xml;q=0.9,application/json"),
                ()->verifyHeaderValues(req,"Accept-Language","ko-KR,ko;q=0.9,zh-CN,zh;q=0.8"),
                ()->verifyHeaderValues(req,"Accept-Encoding","gzip, deflate, sdch"),
                ()->verifyHeaderValues(req,"Connection","keep-alive"),
                ()->verifyHeaderValues(req,"Upgrade-Insecure-Requests","1"),
                ()->verifyHeaderValues(req,"Content-Type","application/x-www-form-urlencoded")
        );
    }

    @Test
    public void parseWrongMessage(){
        assertThrows(InvalidRequestFormatException.class,()->{
            HttpRequestMessage req = requestParser.parse(wrongMessage);
        });
    }

    @Test
    public void queryStringMessage(){
        //given
        String queryStringMessage = withSystemLineSeparator("""
            GET /create?username=hyeonuk&nickname=khu147&password=password1234 HTTP/1.1
            Host: localhost:8080
            User-Agent: Mozilla/5.0
            Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json
            Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8
            Accept-Encoding: gzip, deflate, sdch
            Connection: keep-alive
            Upgrade-Insecure-Requests: 1
            Content-Type: application/x-www-form-urlencoded
            
            """);
        //when
        HttpRequestMessage req = requestParser.parse(queryStringMessage);

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
        String queryStringMessage = withSystemLineSeparator("""
            GET /create?username=&nickname= HTTP/1.1
            Host: localhost:8080
            User-Agent: Mozilla/5.0
            Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json
            Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8
            Accept-Encoding: gzip, deflate, sdch
            Connection: keep-alive
            Upgrade-Insecure-Requests: 1
            Content-Type: application/x-www-form-urlencoded
            
            """);
        //when
        HttpRequestMessage req = requestParser.parse(queryStringMessage);

        //then
        assertAll("blank return blank string",
                ()->assertEquals("",req.getQueryString("username")),
                ()->assertEquals("",req.getQueryString("nickname")));
    }

    @Test
    public void nullQueryStringMessage(){
        //given
        String queryStringMessage = withSystemLineSeparator("""
            GET /create HTTP/1.1
            Host: localhost:8080
            User-Agent: Mozilla/5.0
            Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json
            Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8
            Accept-Encoding: gzip, deflate, sdch
            Connection: keep-alive
            Upgrade-Insecure-Requests: 1
            Content-Type: application/x-www-form-urlencoded
            
            """);
        //when
        HttpRequestMessage req = requestParser.parse(queryStringMessage);

        //then
        assertAll("not exists query parameter return null",
                ()->assertNull(req.getQueryString("notExists")));
    }

    @Test
    public void urlEncodingQueryString(){
        //given
        //안녕하세요 url 인코딩 = %ec%95%88%eb%85%95%ed%95%98%ec%84%b8%ec%9a%94
        String queryStringMessage = withSystemLineSeparator("""
            GET /create?name=%ec%95%88%eb%85%95%ed%95%98%ec%84%b8%ec%9a%94 HTTP/1.1
            Host: localhost:8080
            User-Agent: Mozilla/5.0
            Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json
            Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8
            Accept-Encoding: gzip, deflate, sdch
            Connection: keep-alive
            Upgrade-Insecure-Requests: 1
            Content-Type: application/x-www-form-urlencoded
            
            """);
        //when
        HttpRequestMessage req = requestParser.parse(queryStringMessage);

        //then
        assertEquals("안녕하세요",req.getQueryString("name"));
    }

    private String withSystemLineSeparator(String message){
        return message.replaceAll("\r\n",System.lineSeparator()).replaceAll("\n",System.lineSeparator());
    }
}