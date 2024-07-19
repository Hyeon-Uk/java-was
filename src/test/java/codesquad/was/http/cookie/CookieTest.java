package codesquad.was.http.cookie;

import codesquad.was.http.message.InvalidRequestFormatException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cookie 클래스")
public class CookieTest {

    @Nested
    @DisplayName("생성자는")
    class Constructor {
        @Test
        @DisplayName("기본 값으로 Cookie를 생성한다")
        void defaultConstructorWithNameAndValueTest() {
            //given
            String name = "CustomCookie";
            String value = "CustomCookieValue";

            //when
            Cookie cookie = new Cookie(name, value);

            //then
            assertAll("defaultConstructorValues",
                    () -> assertEquals(name, cookie.getName()),
                    () -> assertEquals(value, cookie.getValue()),
                    () -> assertEquals(-1L, cookie.getMaxAge()),
                    () -> assertEquals("/", cookie.getPath()),
                    () -> assertNull(cookie.getDomain())
            );
        }

        @Test
        @DisplayName("이름이 null일 경우 예외를 발생시킨다")
        void defaultConstructorWithNullNameTest() {
            //given
            String name = null;
            String value = "value";

            //when && then
            assertThrows(InvalidRequestFormatException.class, () -> {
                Cookie cookie = new Cookie(name, value);
            });
        }

        @Test
        @DisplayName("값이 null일 경우 빈 문자열로 설정한다")
        void defaultConstructorWithNullValueTest() {
            //given
            String name = "name";
            String value = null;

            //when
            Cookie cookie = new Cookie(name, value);

            //then
            assertEquals(name, cookie.getName());
            assertEquals("", cookie.getValue());
        }

        @Test
        @DisplayName("이름과 값의 앞뒤 공백을 제거한다")
        void defaultConstructorWithExtraSpaceValueAndName() {
            //given
            String name = "  name ";
            String value = "        value          \r\n";

            //when
            Cookie cookie = new Cookie(name, value);

            //then
            assertEquals("name", cookie.getName());
            assertEquals("value", cookie.getValue());
        }
    }

    @Nested
    @DisplayName("setter 메서드")
    class Setters {
        @Test
        @DisplayName("값을 변경한다")
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
        @DisplayName("도메인을 설정한다")
        void setDomainTest() {
            //given
            String name = "CustomCookie";
            String value = "CustomCookieValue";
            Cookie cookie = new Cookie(name, value);

            //when
            cookie.setDomain("otherDomain");

            //then
            assertEquals("otherDomain", cookie.getDomain());
        }

        @Test
        @DisplayName("최대 수명을 설정한다")
        void setMaxAge() {
            //given
            String name = "CustomCookie";
            String value = "CustomCookieValue";
            Cookie cookie = new Cookie(name, value);

            //when
            cookie.setMaxAge(1000L);

            //then
            assertEquals(1000L, cookie.getMaxAge());
        }

        @Test
        @DisplayName("경로를 설정한다")
        void setPath() {
            //given
            String name = "CustomCookie";
            String value = "CustomCookieValue";
            Cookie cookie = new Cookie(name, value);

            //when
            cookie.setPath("otherPath");

            //then
            assertEquals("otherPath", cookie.getPath());
        }
    }
}