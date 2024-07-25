package codesquad.was.http.message.parser;

import codesquad.message.mock.MockTimer;
import codesquad.was.http.cookie.Cookie;
import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.vo.HttpFile;
import codesquad.was.http.session.SessionManager;
import codesquad.was.http.session.SessionStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestParsingTest {
    private String testGetMessage =
            "GET /index.html HTTP/1.1"+"\r\n"+
            "Host: localhost:8080"+"\r\n"+
            "User-Agent: Mozilla/5.0"+"\r\n"+
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json"+"\r\n"+
            "Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8"+"\r\n"+
            "Accept-Encoding: gzip, deflate, sdch"+"\r\n"+
            "Cookie: name1=value1;name2=value2"+"\r\n"+
            "Connection: keep-alive"+"\r\n"+
            "Upgrade-Insecure-Requests: 1"+"\r\n"+
            "Content-Type: application/x-www-form-urlencoded\r\n\r\n";

    private String wrongMessage =
            "Unknown uri"+"\r\n"+
            "Host: unknown";

    private HttpHeaderParser headerParser = new HttpHeaderParser();
    private HttpQueryStringParser queryStringParser = new HttpQueryStringParser();
    private HttpMultipartParser multipartParser = new HttpMultipartParser();
    private HttpBodyParser bodyParser = new HttpBodyParser(queryStringParser,multipartParser);
    private HttpRequestStartLineParser startLineParser = new HttpRequestStartLineParser(queryStringParser);
    private SessionManager sessionManager = new SessionManager(new SessionStorage(),new MockTimer(10l));
    private HttpRequestParser requestParser = new HttpRequestParser(startLineParser,headerParser,bodyParser,sessionManager);

    private void verifyHeaderValues(HttpRequest req, String key, String valueString){
        List<String> values = Arrays.stream(valueString.split(","))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();

        for(String value : values){
            assertTrue(req.getHeader(key).contains(value));
        }
    }
    @Nested
    @DisplayName("multipart/form-data")
    class MultipartFormDataTest{
        @Test
        void withFileData(){
            //given
            String boundary = "----WebKitFormBoundaryABC123";
            String formKey = "username";
            String formValue = "john_kim";
            String fileKey = "file";
            String fileName = "file.jpg";
            String fileValue = "hello world";

            String body = "--"+boundary+"\r\n" +
                    "Content-Disposition: form-data; name=\""+formKey+"\"\r\n" +
                    "\r\n" +
                    formValue+"\r\n" +
                    "--"+boundary+"\r\n" +
                    "Content-Disposition: form-data; name=\""+fileKey+"\"; filename=\""+fileName+"\"\r\n" +
                    "Content-Type: image/jpeg\r\n" +
                    "\r\n" +
                    fileValue+"\r\n"+
                    "--"+boundary+"--";

            String message = "POST /upload HTTP/1.1\r\n" +
                    "Content-Type: multipart/form-data; boundary="+boundary+"\r\n" +
                    "Content-Length: "+body.getBytes().length+"\r\n"+
                    "\r\n" +
                    body;

            //when
            HttpRequest parse = requestParser.parse(message);

            //then
            assertNotNull(parse.getFile(fileKey));
            HttpFile file = parse.getFile(fileKey);
            assertEquals(fileName,file.getFileName());
            assertTrue(Arrays.equals(fileValue.getBytes(),file.getData()));
            assertEquals(formValue,parse.getQueryString(formKey));
        }
    }

    @Nested
    @DisplayName("Request Cookies")
    class RequestCookies {
        @Test
        void multipleCookieTest() {
            //given
            String getMessage =
                    "GET / HTTP/1.1\r\n" +
                    "Cookie: name1=value1;name2=value2\r\n\r\n";

            //when
            HttpRequest parse = requestParser.parse(new ByteArrayInputStream(getMessage.getBytes()));
            List<Cookie> cookies = parse.getCookies();

            //then
            assertEquals(2,cookies.size());
            assertTrue(cookies.stream()
                    .allMatch(cookie -> cookie.getName().equals("name1") && cookie.getValue().equals("value1") || cookie.getName().equals("name2") && cookie.getValue().equals("value2")));
        }

        @Test
        void blankCookieTest() {
            //given
            String getMessage =
                    "GET / HTTP/1.1\r\n"+
                    "Cookie: \r\n\r\n";

            //when
            HttpRequest parse = requestParser.parse(new ByteArrayInputStream(getMessage.getBytes()));
            List<Cookie> cookies = parse.getCookies();

            //then
            assertEquals(0,cookies.size());
        }

        @Test
        void emptyCookieTest() {
            //given
            String getMessage =
                    "GET / HTTP/1.1\r\n"+
                    "Accept: true\r\n\r\n";

            //when
            HttpRequest parse = requestParser.parse(new ByteArrayInputStream(getMessage.getBytes()));
            List<Cookie> cookies = parse.getCookies();

            //then
            assertEquals(0,cookies.size());
        }
    }

    @Nested
    @DisplayName("with string")
    class WithStringTest {
        @Test
        public void parseGetMessage() throws InvalidRequestFormatException {
            //given
            HttpRequest req = requestParser.parse(testGetMessage);

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
                HttpRequest req = requestParser.parse(wrongMessage);
            });
        }

        @Test
        public void queryStringMessage(){
            //given
            String queryStringMessage =
                "GET /create?username=hyeonuk&nickname=khu147&password=password1234 HTTP/1.1"+"\r\n"+
                "Host: localhost:8080"+"\r\n"+
                "User-Agent: Mozilla/5.0"+"\r\n"+
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json"+"\r\n"+
                "Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8"+"\r\n"+
                "Accept-Encoding: gzip, deflate, sdch"+"\r\n"+
                "Connection: keep-alive"+"\r\n"+
                "Upgrade-Insecure-Requests: 1"+"\r\n\r\n";

            //when
            HttpRequest req = requestParser.parse(queryStringMessage);

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
            String queryStringMessage =
                "GET /create?username=&nickname= HTTP/1.1"+"\r\n"+
                "Host: localhost:8080"+"\r\n"+
                "User-Agent: Mozilla/5.0"+"\r\n"+
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json"+"\r\n"+
                "Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8"+"\r\n"+
                "Accept-Encoding: gzip, deflate, sdch"+"\r\n"+
                "Connection: keep-alive"+"\r\n"+
                "Upgrade-Insecure-Requests: 1"+"\r\n"+
                "Content-Type: application/x-www-form-urlencoded";

            //when
            HttpRequest req = requestParser.parse(queryStringMessage);

            //then
            assertAll("blank return blank string",
                    ()->assertEquals("",req.getQueryString("username")),
                    ()->assertEquals("",req.getQueryString("nickname")));
        }

        @Test
        public void nullQueryStringMessage(){
            //given
            String queryStringMessage =
                "GET /create HTTP/1.1"+"\r\n"+
                "Host: localhost:8080"+"\r\n"+
                "User-Agent: Mozilla/5.0"+"\r\n"+
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json"+"\r\n"+
                "Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8"+"\r\n"+
                "Accept-Encoding: gzip, deflate, sdch"+"\r\n"+
                "Connection: keep-alive"+"\r\n"+
                "Upgrade-Insecure-Requests: 1"+"\r\n"+
                "Content-Type: application/x-www-form-urlencoded";

            //when
            HttpRequest req = requestParser.parse(queryStringMessage);

            //then
            assertAll("not exists query parameter return null",
                    ()->assertNull(req.getQueryString("notExists")));
        }

        @Test
        public void urlEncodingQueryString(){
            //given
            //안녕하세요 url 인코딩 = %ec%95%88%eb%85%95%ed%95%98%ec%84%b8%ec%9a%94
            String queryStringMessage =
                "GET /create?name=%ec%95%88%eb%85%95%ed%95%98%ec%84%b8%ec%9a%94 HTTP/1.1"+"\r\n"+
                "Host: localhost:8080"+"\r\n"+
                "User-Agent: Mozilla/5.0"+"\r\n"+
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json"+"\r\n"+
                "Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8"+"\r\n"+
                "Accept-Encoding: gzip, deflate, sdch"+"\r\n"+
                "Connection: keep-alive"+"\r\n"+
                "Upgrade-Insecure-Requests: 1"+"\r\n"+
                "Content-Type: application/x-www-form-urlencoded";

            //when
            HttpRequest req = requestParser.parse(queryStringMessage);

            //then
            assertEquals("안녕하세요",req.getQueryString("name"));
        }

        @Test
        public void bodyQueryStringTest(){
            //given
            //안녕하세요 url 인코딩 = %ec%95%88%eb%85%95%ed%95%98%ec%84%b8%ec%9a%94
            String body = "message=%ec%95%88%eb%85%95%ed%95%98%ec%84%b8%ec%9a%94&nickname=hyeonuk";
            String message =
                    "POST /user/create HTTP/1.1"+"\r\n"+
                    "HOST : localhost:8080"+"\r\n"+
                    "Content-Length: "+body.getBytes().length+"\r\n"+
                    "Content-Type: application/x-www-form-urlencoded"+"\r\n"+
                    "\r\n"+body;
            //when
            HttpRequest req = requestParser.parse(message);

            //then
            assertAll("body queryString extract",
                    ()->assertEquals("안녕하세요",req.getQueryString("message")),
                    ()->assertEquals("hyeonuk",req.getQueryString("nickname")));

        }
    }

    @Nested
    @DisplayName("with inputStream")
    class WithInputStream {
        @Test
        public void parseGetMessage() throws InvalidRequestFormatException {
            //given
            InputStream is = new ByteArrayInputStream(testGetMessage.getBytes());
            HttpRequest req = requestParser.parse(is);

            //when & then
            assertAll("request message validation",
                    () -> assertEquals(HttpMethod.GET, req.getMethod()),
                    () -> assertEquals("/index.html", req.getUri()),
                    () -> assertEquals("HTTP/1.1", req.getHttpVersion()),
                    () -> assertTrue(req.getHeader("Host").contains("localhost:8080")),
                    () -> assertTrue(req.getHeader("User-Agent").contains("Mozilla/5.0")),
                    () -> verifyHeaderValues(req, "Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,application/json"),
                    () -> verifyHeaderValues(req, "Accept-Language", "ko-KR,ko;q=0.9,zh-CN,zh;q=0.8"),
                    () -> verifyHeaderValues(req, "Accept-Encoding", "gzip, deflate, sdch"),
                    () -> verifyHeaderValues(req, "Connection", "keep-alive"),
                    () -> verifyHeaderValues(req, "Upgrade-Insecure-Requests", "1"),
                    () -> verifyHeaderValues(req, "Content-Type", "application/x-www-form-urlencoded")
            );
        }

        @Test
        public void parseWrongMessage() {
            assertThrows(InvalidRequestFormatException.class, () -> {
                HttpRequest req = requestParser.parse(new ByteArrayInputStream(wrongMessage.getBytes()));
            });
        }

        @Test
        public void queryStringMessage() {
            //given
            String queryStringMessage =
                    "GET /create?username=hyeonuk&nickname=khu147&password=password1234 HTTP/1.1" + "\r\n" +
                            "Host: localhost:8080" + "\r\n" +
                            "User-Agent: Mozilla/5.0" + "\r\n" +
                            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json" + "\r\n" +
                            "Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8" + "\r\n" +
                            "Accept-Encoding: gzip, deflate, sdch" + "\r\n" +
                            "Connection: keep-alive" + "\r\n" +
                            "Upgrade-Insecure-Requests: 1" + "\r\n" +
                            "Content-Type: application/x-www-form-urlencoded\r\n\r\n";

            //when
            HttpRequest req = requestParser.parse(new ByteArrayInputStream(queryStringMessage.getBytes()));

            //then
            assertAll("queryStringValidation",
                    () -> assertEquals("hyeonuk", req.getQueryString("username")),
                    () -> assertEquals("khu147", req.getQueryString("nickname")),
                    () -> assertEquals("password1234", req.getQueryString("password"))
            );
        }

        @Test
        public void emptyQueryStringMessage() {
            //given
            String queryStringMessage =
                    "GET /create?username=&nickname= HTTP/1.1" + "\r\n" +
                            "Host: localhost:8080" + "\r\n" +
                            "User-Agent: Mozilla/5.0" + "\r\n" +
                            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json" + "\r\n" +
                            "Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8" + "\r\n" +
                            "Accept-Encoding: gzip, deflate, sdch" + "\r\n" +
                            "Connection: keep-alive" + "\r\n" +
                            "Upgrade-Insecure-Requests: 1" + "\r\n" +
                            "Content-Type: application/x-www-form-urlencoded";

            //when
            HttpRequest req = requestParser.parse(new ByteArrayInputStream(queryStringMessage.getBytes()));

            //then
            assertAll("blank return blank string",
                    () -> assertEquals("", req.getQueryString("username")),
                    () -> assertEquals("", req.getQueryString("nickname")));
        }

        @Test
        public void nullQueryStringMessage() {
            //given
            String queryStringMessage =
                    "GET /create HTTP/1.1" + "\r\n" +
                            "Host: localhost:8080" + "\r\n" +
                            "User-Agent: Mozilla/5.0" + "\r\n" +
                            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json" + "\r\n" +
                            "Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8" + "\r\n" +
                            "Accept-Encoding: gzip, deflate, sdch" + "\r\n" +
                            "Connection: keep-alive" + "\r\n" +
                            "Upgrade-Insecure-Requests: 1" + "\r\n" +
                            "Content-Type: application/x-www-form-urlencoded";

            //when
            HttpRequest req = requestParser.parse(new ByteArrayInputStream(queryStringMessage.getBytes()));

            //then
            assertAll("not exists query parameter return null",
                    () -> assertNull(req.getQueryString("notExists")));
        }

        @Test
        public void urlEncodingQueryString() {
            //given
            //안녕하세요 url 인코딩 = %ec%95%88%eb%85%95%ed%95%98%ec%84%b8%ec%9a%94
            String queryStringMessage =
                    "GET /create?name=%ec%95%88%eb%85%95%ed%95%98%ec%84%b8%ec%9a%94 HTTP/1.1" + "\r\n" +
                            "Host: localhost:8080" + "\r\n" +
                            "User-Agent: Mozilla/5.0" + "\r\n" +
                            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,application/json" + "\r\n" +
                            "Accept-Language: ko-KR,ko;q=0.9,zh-CN,zh;q=0.8" + "\r\n" +
                            "Accept-Encoding: gzip, deflate, sdch" + "\r\n" +
                            "Connection: keep-alive" + "\r\n" +
                            "Upgrade-Insecure-Requests: 1" + "\r\n" +
                            "Content-Type: application/x-www-form-urlencoded";

            //when
            HttpRequest req = requestParser.parse(new ByteArrayInputStream(queryStringMessage.getBytes()));

            //then
            assertEquals("안녕하세요", req.getQueryString("name"));
        }

        @Test
        public void bodyQueryStringTest() {
            //given
            //안녕하세요 url 인코딩 = %ec%95%88%eb%85%95%ed%95%98%ec%84%b8%ec%9a%94
            String body = "message=%ec%95%88%eb%85%95%ed%95%98%ec%84%b8%ec%9a%94&nickname=hyeonuk";
            String message =
                    "POST /user/create HTTP/1.1" + "\r\n" +
                            "HOST : localhost:8080" + "\r\n" +
                            "Content-Type: application/x-www-form-urlencoded" + "\r\n" +
                            "Content-Length: "+body.getBytes().length+"\r\n"+
                            "\r\n" +
                            body;
            //when
            HttpRequest req = requestParser.parse(new ByteArrayInputStream(message.getBytes()));

            //then
            assertAll("body queryString extract",
                    () -> assertEquals("안녕하세요", req.getQueryString("message")),
                    () -> assertEquals("hyeonuk", req.getQueryString("nickname")));

        }
    }
}