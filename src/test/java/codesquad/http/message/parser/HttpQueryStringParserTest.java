package codesquad.http.message.parser;

import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpQueryStringParserTest {
    private final HttpQueryStringParser parser = new HttpQueryStringParser();

    @Test
    void parseWithQueryString() {
        //given
        String uri = "/?id=abc&pw=password";

        //when
        Map<String, String> parse = parser.parse(uri);

        //then
        assertAll("queryString",
                () -> assertEquals("abc", parse.get("id")),
                () -> assertEquals("password", parse.get("pw"))
        );
    }

    @Test
    void parseWithEmtpyQueryValue() {
        //given
        String uri = "/?id=&pw=password";

        //when
        Map<String, String> parse = parser.parse(uri);

        //then
        assertAll("emptyValue",
                () -> assertTrue(parse.get("id").isEmpty()),
                () -> assertEquals("password", parse.get("pw"))
        );
    }

    @Test
    void getWithNotExistsKey(){
        //given
        String uri = "/?";

        //when
        Map<String, String> parse = parser.parse(uri);

        //then
        assertNull(parse.get("notExistsKey"));
    }
}