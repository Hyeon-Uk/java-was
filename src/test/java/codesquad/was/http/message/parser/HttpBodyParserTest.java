package codesquad.was.http.message.parser;

import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.parser.mock.MockHttpMultipartParser;
import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.message.vo.HttpHeader;
import codesquad.was.http.message.vo.MIME;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("HttpBodyParser 클래스")
class HttpBodyParserTest {

    // Data
    private HttpMultipartParser multipartParser;
    private HttpQueryStringParser queryStringParser;
    private HttpBodyParser parser;

    // Context setup
    @BeforeEach
    void setUp() {
        multipartParser = new MockHttpMultipartParser();
        queryStringParser = new HttpQueryStringParser();
        parser = new HttpBodyParser(queryStringParser, multipartParser);
    }

    @Nested
    @DisplayName("parse 메소드는")
    class ParseMethod {

        @Test
        @DisplayName("문자열 본문을 올바르게 파싱한다")
        void bodyParsingWithString() {
            String body = "hello world";
            HttpBody parse = parser.parse(body);
            assertEquals(body, new String(parse.getBody()));
        }

        @Test
        @DisplayName("바이트 배열 본문을 올바르게 파싱한다")
        void bodyParsingWithByteArray() {
            byte[] body = "hello world".getBytes();
            HttpBody parse = parser.parse(body);
            assertEquals(body, parse.getBody());
        }
    }

    @Nested
    @DisplayName("parse 메소드는 헤더와 InputStream으로")
    class ParseWithHeaderAndIS {

        @Test
        @DisplayName("복수의 Content-Length 헤더가 있으면 예외를 발생시킨다")
        void moreThanTwoContentLengthHeaders() {
            HttpHeader header = new HttpHeader();
            header.addHeader("Content-Length", "1");
            header.addHeader("Content-Length", "2");
            header.addHeader("Content-Length", "3");

            assertThrows(InvalidRequestFormatException.class, () ->
                    parser.parse(header, new ByteArrayInputStream("".getBytes()))
            );
        }

        @Test
        @DisplayName("multipart/form-data 헤더를 올바르게 처리한다")
        void withMultipartFormDataHeader() {
            String body = "hello world";
            String contentType = MIME.MULTIPART_FORM_DATA.getMimeType();
            int length = body.getBytes().length;
            HttpHeader header = new HttpHeader();
            header.setHeader("Content-Type", contentType);
            header.setHeader("Content-Length", String.valueOf(length));

            InputStream is = new ByteArrayInputStream(body.getBytes());

            HttpBody parse = parser.parse(header, is);

            assertEquals("hyeonuk", parse.getQueryString().get("id"));
            assertEquals(MockHttpMultipartParser.mockFile.getFileName(), parse.getFile().get("file").getFileName());
            assertEquals(MockHttpMultipartParser.mockFile.getData(), parse.getFile().get("file").getData());
        }

        @Test
        @DisplayName("InputStream에서 IOException이 발생하면 예외를 발생시킨다")
        void withIOException() {
            InputStream is = new InputStream() {
                @Override
                public int read() throws IOException {
                    throw new IOException("ioexception");
                }
            };
            HttpHeader header = new HttpHeader();
            header.addHeader("Content-Length", "1");

            assertThrows(InvalidRequestFormatException.class, () ->
                    parser.parse(header, is)
            );
        }
    }
}