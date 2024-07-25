package codesquad.was.http.cookie;

import codesquad.was.http.message.InvalidRequestFormatException;
import org.junit.jupiter.api.Test;

import java.net.HttpCookie;

import static org.junit.jupiter.api.Assertions.*;

public class CookieTest {
    @Test
    void defaultConstructorWithNameAndValueTest() {
        //given
        String name = "CustomCookie";
        String value = "CustomCookieValue";

        //when
        Cookie cookie = new Cookie(name, value);
        HttpCookie hCookie = new HttpCookie("hello","world");
        hCookie.getDomain();
        //then
        assertAll("defaultConstructorValues",
                () -> assertEquals(name, cookie.getName()),
                () -> assertEquals(value, cookie.getValue()),
                () -> assertEquals(-1l, cookie.getMaxAge()),
                () -> assertEquals("/", cookie.getPath()),
                () -> assertNull(cookie.getDomain())
        );
    }

    @Test
    void defaultConstructorWithNullNameTest() {
        //given
        String name = null;
        String value = "value";

        //when && then
        assertThrows(InvalidRequestFormatException.class,()->{
            Cookie cookie = new Cookie(name,value);
        });
    }

    @Test
    void defaultConstructorWithNullValueTest() {
        //given
        String name = "name";
        String value = null;

        //when
        Cookie cookie = new Cookie(name,value);

        //then
        assertEquals(name,cookie.getName());
        assertEquals("",cookie.getValue());
    }

    @Test
    void defaultConstructorWithExtraSpaceValueAndName(){
        //given
        String name = "  name ";
        String value = "        value          \r\n";

        //when
        Cookie cookie = new Cookie(name,value);

        //then
        assertEquals("name",cookie.getName());
        assertEquals("value",cookie.getValue());
    }

    @Test
    void setValueTest() {
        //given
        String name = "CustomCookie";
        String value = "CustomCookieValue";
        Cookie cookie = new Cookie(name, value);

        //when
        cookie.setValue("exchange");

        //then
        assertEquals("exchange", cookie.getValue());
    }

    @Test
    void setDomainTest(){
        //given
        String name = "CustomCookie";
        String value = "CustomCookieValue";
        Cookie cookie = new Cookie(name, value);

        //when
        cookie.setDomain("otherDomain");

        //then
        assertEquals("otherDomain",cookie.getDomain());
    }

    @Test
    void setMaxAge(){
        //given
        String name = "CustomCookie";
        String value = "CustomCookieValue";
        Cookie cookie = new Cookie(name, value);

        //when
        cookie.setMaxAge(1000l);

        //then
        assertEquals(1000l,cookie.getMaxAge());
    }

    @Test
    void setPath(){
        //given
        String name = "CustomCookie";
        String value = "CustomCookieValue";
        Cookie cookie = new Cookie(name, value);

        //when
        cookie.setPath("otherPath");

        //then
        assertEquals("otherPath",cookie.getPath());
    }
}
