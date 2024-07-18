package codesquad.was.http.message.parser;

import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.vo.HttpHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpHeaderParserTest {
    private final HttpHeaderParser parser = new HttpHeaderParser();

    @Nested
    @DisplayName("with string")
    class WithString {
        @Test
        void withSingleValue() {
            //given
            String[] headerLine = {"hello: world", "welcome: java"};

            //when
            HttpHeader parse = parser.parse(headerLine);

            //then
            assertAll("singleValue",
                    () -> assertTrue(parse.getHeaders("hello").contains("world")),
                    () -> assertTrue(parse.getHeaders("welcome").contains("java"))
            );
        }

        @Test
        void withMultiValues() {
            //given
            String[] headerLine = {"hello: world, and, java"};

            //when
            HttpHeader parse = parser.parse(headerLine);

            //then
            assertAll("multiValue",
                    () -> assertTrue(parse.getHeaders("hello").contains("world")),
                    () -> assertTrue(parse.getHeaders("hello").contains("and")),
                    () -> assertTrue(parse.getHeaders("hello").contains("java"))
            );
        }

        @Test
        void allHeadersTest() {
            //given
            String[] headerLine = {"id: secret", "hello: world, and, java"};

            //when
            HttpHeader parse = parser.parse(headerLine);

            Map<String, List<String>> headers = parse.allHeaders();

            //then
            assertAll("multiValue",
                    () -> assertTrue(headers.get("hello").contains("world")),
                    () -> assertTrue(headers.get("hello").contains("and")),
                    () -> assertTrue(headers.get("hello").contains("java")),
                    () -> assertTrue(headers.get("id").contains("secret"))
            );
        }

        @Test
        void withEmptyValue() {
            //given
            String[] headerLine = {"hello: "};

            //when
            HttpHeader parse = parser.parse(headerLine);

            //then
            assertEquals(0, parse.getHeaders("hello").size());
        }

        @Test
        void getHeaderWithNotExistedKey() {
            //given

            //when
            HttpHeader parse = parser.parse(new String[]{});

            //then
            assertEquals(0, parse.getHeaders("notExistsKey").size());
        }
    }

    @Nested
    @DisplayName("with input stream")
    class WithInputStream {
        @Test
        void withSingleValue() {
            //given
            String headerLine = "hello: world\r\nwelcome: java\r\n\r\n";
            InputStream is = new ByteArrayInputStream(headerLine.getBytes());

            //when
            HttpHeader parse = parser.parse(is);

            //then
            assertAll("singleValue",
                    () -> assertTrue(parse.getHeaders("hello").contains("world")),
                    () -> assertTrue(parse.getHeaders("welcome").contains("java"))
            );
        }

        @Test
        void withMultiValues() {
            //given
            String headerLine = "hello: world, and, java\r\nwelcome: java\r\n\r\n";
            InputStream is = new ByteArrayInputStream(headerLine.getBytes());

            //when
            HttpHeader parse = parser.parse(is);

            //then
            assertAll("multiValue",
                    () -> assertTrue(parse.getHeaders("hello").contains("world")),
                    () -> assertTrue(parse.getHeaders("hello").contains("and")),
                    () -> assertTrue(parse.getHeaders("hello").contains("java"))
            );
        }

        @Test
        void allHeadersTest() {
            //given
            String headerLine = "id: secret\r\nhello: world, and, java\r\n\r\n";
            InputStream is = new ByteArrayInputStream(headerLine.getBytes());

            //when
            HttpHeader parse = parser.parse(is);

            Map<String, List<String>> headers = parse.allHeaders();

            //then
            assertAll("multiValue",
                    () -> assertTrue(headers.get("hello").contains("world")),
                    () -> assertTrue(headers.get("hello").contains("and")),
                    () -> assertTrue(headers.get("hello").contains("java")),
                    () -> assertTrue(headers.get("id").contains("secret"))
            );
        }

        @Test
        void withEmptyValue() {
            //given
            String headerLine = "hello: \r\n\r\n";
            InputStream is = new ByteArrayInputStream(headerLine.getBytes());

            //when
            HttpHeader parse = parser.parse(is);

            //then
            assertEquals(0, parse.getHeaders("hello").size());
        }

        @Test
        void getHeaderWithNotExistedKey() {
            //given
            String headerLine = "hello: world\r\n";
            InputStream is = new ByteArrayInputStream(headerLine.getBytes());

            //when
            HttpHeader parse = parser.parse(is);

            //then
            assertEquals(0, parse.getHeaders("notExistsKey").size());
        }

        @Test
        void throwIOExceptionTest(){
            //given
            InputStream is = new InputStream() {
                @Override
                public int read() throws IOException {
                    throw new IOException("ioexception");
                }
            };

            //when & then
            assertThrows(InvalidRequestFormatException.class,()->{
                parser.parse(is);
            });
        }
    }
}