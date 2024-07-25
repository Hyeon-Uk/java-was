package codesquad.was.http.message.response;

import codesquad.was.http.cookie.Cookie;
import codesquad.was.http.exception.HttpException;
import codesquad.was.http.exception.HttpInternalServerErrorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpResponseTest {
    private HttpResponse response;
    private Map<String, List<String>> header;
    @BeforeEach
    void setUp(){
        header = new HashMap<>();
        response = new HttpResponse("HTTP/1.1",header);
    }

    @Test
    void defaultTest(){
        //given

        //when

        //then
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(0,response.getBody().length);
    }

    @Test
    void sendRedirect(){
        //given

        //when
        response.sendRedirect("/redirectUri");

        //then
        assertEquals(HttpStatus.FOUND, response.getStatus());
        assertEquals("/redirectUri",response.getHeaders("Location").get(0));
    }

    @Test
    void addCookie(){
        //given
        Cookie cookie1 = new Cookie("cookie1","value1");
        cookie1.setDomain("example.com");
        cookie1.setMaxAge(1000l);
        Cookie cookie2 = new Cookie("cookie2","value2");
        String expected =
                "HTTP/1.1 200 OK"+"\r\n"+
                "Set-Cookie: cookie1=value1; Path=/; Domain=example.com; Max-Age=1000"+"\r\n"+
                "Set-Cookie: cookie2=value2; Path=/"+"\r\n"+
                "\r\n";

        //when
        response.addCookie(cookie1);
        response.addCookie(cookie2);

        //then
        String actual = new String(response.parse());
        assertEquals(expected,actual);
    }

    @Test
    void constructorWithHttpException(){
        //given
        String message = "message!";
        HttpException httpException = new HttpInternalServerErrorException(message);

        //when
        response = new HttpResponse(httpException);

        //then
        assertEquals(httpException.getStatus(),response.getStatus());
        assertEquals(httpException.getErrorMessage(),new String(response.getBody()));
    }
}