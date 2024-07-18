package codesquad.was.http.message.parser;

import codesquad.was.http.exception.HttpInternalServerErrorException;
import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.vo.HttpRequestStartLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestStartLineParserTest {
    private final HttpQueryStringParser queryStringParser = new HttpQueryStringParser();
    private final HttpRequestStartLineParser parser = new HttpRequestStartLineParser(queryStringParser);

    @Nested
    @DisplayName("with string")
    class WithString {
        @Test
        void startLineParsingTest() {
            //given
            String startLine = "GET / HTTP/1.1\r\n";

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
            String startLine = "GET /?hello=".concat(URLEncoder.encode(value)).concat(" HTTP/1.1\r\n");

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
        void parsingWithoutMethod() {
            //given
            String startLine = "/ HTTP/1.1\r\n";

            //when & then
            assertThrows(InvalidRequestFormatException.class, () -> {
                parser.parse(startLine);
            });
        }

        @Test
        void parsingWithoutUri() {
            //given
            String startLine = "POST HTTP/1.1\r\n";

            //when & then
            assertThrows(InvalidRequestFormatException.class, () -> {
                parser.parse(startLine);
            });
        }

        @Test
        void parsingWithoutHttpVersion() {
            //given
            String startLine = "GET / \r\n";

            //when & then
            assertThrows(InvalidRequestFormatException.class, () -> {
                parser.parse(startLine);
            });
        }

        @Test
        void parsingWithWrongMethod() {
            //given
            String startLine = "UNKNOWN / HTTP/1.1";

            //when & then
            assertThrows(InvalidRequestFormatException.class, () -> {
                parser.parse(startLine);
            });
        }
    }

    @Nested
    @DisplayName("with InputStream")
    class WithInputStream {
        @Test
        void startLineParsingTest() throws IOException {
            //given
            String startLine = "GET / HTTP/1.1\r\n";
            InputStream is = new ByteArrayInputStream(startLine.getBytes());

            //when
            HttpRequestStartLine parse = parser.parse(is);

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
            String startLine = "GET /?hello=".concat(URLEncoder.encode(value)).concat(" HTTP/1.1\r\n");
            InputStream is = new ByteArrayInputStream(startLine.getBytes());

            //when
            HttpRequestStartLine parse = parser.parse(is);

            //then
            assertAll("startLineWithURLEncodedString",
                    () -> assertEquals(HttpMethod.GET, parse.getMethod()),
                    () -> assertEquals("/", parse.getUri()),
                    () -> assertEquals("HTTP/1.1", parse.getHttpVersion())
            );
        }

        @Test
        void parsingWithoutMethod() {
            //given
            String startLine = "/ HTTP/1.1\r\n";
            InputStream is = new ByteArrayInputStream(startLine.getBytes());

            //when & then
            assertThrows(InvalidRequestFormatException.class, () -> {
                parser.parse(is);
            });
        }

        @Test
        void parsingWithoutUri() {
            //given
            String startLine = "POST HTTP/1.1\r\n";
            InputStream is = new ByteArrayInputStream(startLine.getBytes());

            //when & then
            assertThrows(InvalidRequestFormatException.class, () -> {
                parser.parse(is);
            });
        }

        @Test
        void parsingWithoutHttpVersion() {
            //given
            String startLine = "GET / \r\n";
            InputStream is = new ByteArrayInputStream(startLine.getBytes());

            //when & then
            assertThrows(InvalidRequestFormatException.class, () -> {
                parser.parse(is);
            });
        }

        @Test
        void parsingWithWrongMethod() {
            //given
            String startLine = "UNKNOWN / HTTP/1.1\r\n";
            InputStream is = new ByteArrayInputStream(startLine.getBytes());

            //when & then
            assertThrows(InvalidRequestFormatException.class, () -> {
                parser.parse(is);
            });
        }

        @Test
        void parsingWithoutCRLF(){
            //given
            String startLine = "GET / HTTP/1.1";
            InputStream is = new ByteArrayInputStream(startLine.getBytes());

            //when
            HttpRequestStartLine parse = parser.parse(is);

            //then
            assertAll("startLineWithURLEncodedString",
                    () -> assertEquals(HttpMethod.GET, parse.getMethod()),
                    () -> assertEquals("/", parse.getUri()),
                    () -> assertEquals("HTTP/1.1", parse.getHttpVersion())
            );
        }

        @Test
        void withIOException(){
            //given
            InputStream is = new InputStream() {
                @Override
                public int read() throws IOException {
                    throw new IOException();
                }
            };

            //when & then
            assertThrows(InvalidRequestFormatException.class,()->{
                parser.parse(is);
            });
        }
    }
}