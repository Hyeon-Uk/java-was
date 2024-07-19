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

@DisplayName("HttpRequestParser 클래스")
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
    @DisplayName("multipart/form-data 요청을")
    class MultipartFormDataTest{
        @Test
        @DisplayName("File 도 정상적으로 처리할 수 있다.")
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
    @DisplayName("Cookie 들의 요청을 받으면")
    class RequestCookies {
        @Test
        @DisplayName("여러개의 쿠키도 잘 받아올 수 있다.")
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
        @DisplayName("쿠키의 값이 비어있으면 파싱을 하지 않는다")
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
        @DisplayName("쿠키 헤더가 없으면 파싱을 하지 않는다.")
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
    @DisplayName("request message가 string 형태이면")
    class WithStringTest {
        @Test
        @DisplayName("제대로 된 포멧을 잘 파싱한다.")
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
        @DisplayName("잘못된 포멧은 exception을 던진다.")
        public void parseWrongMessage(){
            assertThrows(InvalidRequestFormatException.class,()->{
                HttpRequest req = requestParser.parse(wrongMessage);
            });
        }

        @Test
        @DisplayName("URI의 쿼리 스트링을 파싱할 수 있다.")
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
        @DisplayName("URI의 쿼리스트링이 비어있으면 값을 파싱하지 않는다.")
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
        @DisplayName("URI에 쿼리스트링이 없으면 쿼리스트링이 비어있다.")
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
        @DisplayName("URL Encoding된 쿼리스트링을 decoding하여 받을 수 있다.")
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
        @DisplayName("Body에 쿼리스트링 요청이 들어와도 쿼리스트링을 파싱할 수 있다.")
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
    @DisplayName("InputStream으로 파싱 요청이 들어오면")
    class WithInputStream {
        @Test
        @DisplayName("제대로된 포멧을 파싱할 수 있다.")
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
        @DisplayName("포멧이 깨지면 exception을 던진다.")
        public void parseWrongMessage() {
            assertThrows(InvalidRequestFormatException.class, () -> {
                HttpRequest req = requestParser.parse(new ByteArrayInputStream(wrongMessage.getBytes()));
            });
        }

        @Test
        @DisplayName("URI의 쿼리 스트링을 파싱할 수 있다.")
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
        @DisplayName("URI의 쿼리스트링 벨류가 비어있으면 값을 파싱하지 않는다.")
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
        @DisplayName("URI에 쿼리스트링이 없으면 없다.")
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
        @DisplayName("URL 인코딩된 쿼리스트링을 디코딩하여 받을 수 있다.")
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
        @DisplayName("Body에도 인코딩된 쿼리스트링을 파싱할 수 있다.")
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