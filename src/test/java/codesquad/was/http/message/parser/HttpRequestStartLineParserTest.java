package codesquad.was.http.message.parser;

import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.vo.HttpRequestStartLine;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestStartLineParserTest {
    private final HttpRequestStartLineParser parser = new HttpRequestStartLineParser();

    @Test
    void startLineParsingTest() {
        //given
        String startLine = "GET / HTTP/1.1";

        //when
        HttpRequestStartLine parse = parser.parse(startLine);

        //then
        assertAll("startLine",
                () -> assertEquals(HttpMethod.GET, parse.getMethod()),
                () -> assertEquals("/", parse.getUri()),
                () -> assertEquals("HTTP/1.1", parse.getHttpVersion())
        );
    }

    @Test
    void startLineWithEncodedQueryString() {
        //given
        String value = "world";
        String startLine = "GET /?hello=".concat(URLEncoder.encode(value)).concat(" HTTP/1.1");

        //when
        HttpRequestStartLine parse = parser.parse(startLine);

        //then
        assertAll("startLineWithURLEncodedString",
                () -> assertEquals(HttpMethod.GET, parse.getMethod()),
                () -> assertEquals("/", parse.getUri()),
                () -> assertEquals("HTTP/1.1", parse.getHttpVersion())
        );
    }

    @Test
    void parsingWithoutMethod(){
        //given
        String startLine = "/ HTTP/1.1";

        //when & then
        assertThrows(InvalidRequestFormatException.class,()->{
           parser.parse(startLine);
        });
    }
    @Test
    void parsingWithoutUri(){
        //given
        String startLine = "POST HTTP/1.1";

        //when & then
        assertThrows(InvalidRequestFormatException.class,()->{
            parser.parse(startLine);
        });
    }

    @Test
    void parsingWithoutHttpVersion(){
        //given
        String startLine = "GET / ";

        //when & then
        assertThrows(InvalidRequestFormatException.class,()->{
            parser.parse(startLine);
        });
    }

    @Test
    void parsingWithWrongMethod(){
        //given
        String startLine = "UNKNOWN / HTTP/1.1";

        //when & then
        assertThrows(InvalidRequestFormatException.class,()->{
            parser.parse(startLine);
        });
    }
}