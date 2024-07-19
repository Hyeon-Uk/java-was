package codesquad.was.http.message.parser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HttpQueryStringParser 클래스")
class HttpQueryStringParserTest {
    private final HttpQueryStringParser parser = new HttpQueryStringParser();

    @Nested
    @DisplayName("parse 메소드는")
    class ParseMethod {

        @Test
        @DisplayName("쿼리 스트링이 포함된 URI를 파싱한다")
        void parseWithQueryString() {
            // given
            String uri = "id=abc&pw=password";

            // when
            Map<String, String> parse = parser.parse(uri);

            // then
            assertAll("queryString",
                    () -> assertEquals("abc", parse.get("id")),
                    () -> assertEquals("password", parse.get("pw"))
            );
        }

        @Test
        @DisplayName("빈 값이 있는 쿼리 스트링을 파싱한다")
        void parseWithEmptyQueryValue() {
            // given
            String uri = "id=&pw=password";

            // when
            Map<String, String> parse = parser.parse(uri);

            // then
            assertAll("emptyValue",
                    () -> assertTrue(parse.get("id").isEmpty()),
                    () -> assertEquals("password", parse.get("pw"))
            );
        }

        @Test
        @DisplayName("존재하지 않는 키를 가져온다")
        void getWithNotExistsKey() {
            // given
            String uri = "";

            // when
            Map<String, String> parse = parser.parse(uri);

            // then
            assertNull(parse.get("notExistsKey"));
        }
    }
}
