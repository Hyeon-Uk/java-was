package codesquad.was.http.message.parser;

import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.vo.HttpHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HttpHeaderParser 테스트")
class HttpHeaderParserTest {
    private HttpHeaderParser parser;

    @BeforeEach
    void setUp() {
        parser = new HttpHeaderParser();
    }

    @Nested
    @DisplayName("문자열 입력으로 파싱할 때")
    class ParseFromString {
        @Test
        @DisplayName("단일 값을 가진 헤더를 파싱한다")
        void parseSingleValueHeaders() {
            // Given
            String[] headerLines = {"hello: world", "welcome: java"};

            // When
            HttpHeader header = parser.parse(headerLines);

            // Then
            assertAll(
                    () -> assertEquals(List.of("world"), header.getHeaders("hello")),
                    () -> assertEquals(List.of("java"), header.getHeaders("welcome"))
            );
        }

        @Test
        @DisplayName("다중 값을 가진 헤더를 파싱한다")
        void parseMultiValueHeaders() {
            // Given
            String[] headerLines = {"hello: world, and, java"};

            // When
            HttpHeader header = parser.parse(headerLines);

            // Then
            assertEquals(List.of("world", "and", "java"), header.getHeaders("hello"));
        }

        @Test
        @DisplayName("모든 헤더를 파싱하고 반환한다")
        void parseAllHeaders() {
            // Given
            String[] headerLines = {"id: secret", "hello: world, and, java"};

            // When
            HttpHeader header = parser.parse(headerLines);
            Map<String, List<String>> headers = header.allHeaders();

            // Then
            assertAll(
                    () -> assertEquals(List.of("secret"), headers.get("id")),
                    () -> assertEquals(List.of("world", "and", "java"), headers.get("hello"))
            );
        }

        @Test
        @DisplayName("빈 값을 가진 헤더를 파싱한다")
        void parseEmptyValueHeader() {
            // Given
            String[] headerLines = {"hello: "};

            // When
            HttpHeader header = parser.parse(headerLines);

            // Then
            assertTrue(header.getHeaders("hello").isEmpty());
        }

        @Test
        @DisplayName("존재하지 않는 키로 헤더를 조회한다")
        void getNotExistingHeader() {
            // Given
            HttpHeader header = parser.parse(new String[]{});

            // When & Then
            assertTrue(header.getHeaders("notExistsKey").isEmpty());
        }
    }

    @Nested
    @DisplayName("InputStream 입력으로 파싱할 때")
    class ParseFromInputStream {
        @Test
        @DisplayName("단일 값을 가진 헤더를 파싱한다")
        void parseSingleValueHeaders() {
            // Given
            InputStream is = createInputStream("hello: world\r\nwelcome: java\r\n\r\n");

            // When
            HttpHeader header = parser.parse(is);

            // Then
            assertAll(
                    () -> assertEquals(List.of("world"), header.getHeaders("hello")),
                    () -> assertEquals(List.of("java"), header.getHeaders("welcome"))
            );
        }

        @Test
        @DisplayName("다중 값을 가진 헤더를 파싱한다")
        void parseMultiValueHeaders() {
            // Given
            InputStream is = createInputStream("hello: world, and, java\r\nwelcome: java\r\n\r\n");

            // When
            HttpHeader header = parser.parse(is);

            // Then
            assertEquals(List.of("world", "and", "java"), header.getHeaders("hello"));
        }

        @Test
        @DisplayName("모든 헤더를 파싱하고 반환한다")
        void parseAllHeaders() {
            // Given
            InputStream is = createInputStream("id: secret\r\nhello: world, and, java\r\n\r\n");

            // When
            HttpHeader header = parser.parse(is);
            Map<String, List<String>> headers = header.allHeaders();

            // Then
            assertAll(
                    () -> assertEquals(List.of("secret"), headers.get("id")),
                    () -> assertEquals(List.of("world", "and", "java"), headers.get("hello"))
            );
        }

        @Test
        @DisplayName("빈 값을 가진 헤더를 파싱한다")
        void parseEmptyValueHeader() {
            // Given
            InputStream is = createInputStream("hello: \r\n\r\n");

            // When
            HttpHeader header = parser.parse(is);

            // Then
            assertTrue(header.getHeaders("hello").isEmpty());
        }

        @Test
        @DisplayName("존재하지 않는 키로 헤더를 조회한다")
        void getNotExistingHeader() {
            // Given
            InputStream is = createInputStream("hello: world\r\n");

            // When
            HttpHeader header = parser.parse(is);

            // Then
            assertTrue(header.getHeaders("notExistsKey").isEmpty());
        }

        @Test
        @DisplayName("IOException 발생 시 InvalidRequestFormatException을 던진다")
        void throwInvalidRequestFormatExceptionOnIOException() {
            // Given
            InputStream is = new InputStream() {
                @Override
                public int read() throws IOException {
                    throw new IOException("ioexception");
                }
            };

            // When & Then
            assertThrows(InvalidRequestFormatException.class, () -> parser.parse(is));
        }

        private InputStream createInputStream(String content) {
            return new ByteArrayInputStream(content.getBytes());
        }
    }
}