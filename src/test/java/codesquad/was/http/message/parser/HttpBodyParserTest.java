package codesquad.was.http.message.parser;

import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.parser.mock.MockHttpMultipartParser;
import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.message.vo.HttpFile;
import codesquad.was.http.message.vo.HttpHeader;
import codesquad.was.http.message.vo.MIME;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HttpBodyParserTest {
    private final HttpMultipartParser multipartParser = new MockHttpMultipartParser();
    private final HttpQueryStringParser queryStringParser = new HttpQueryStringParser();
    private final HttpBodyParser parser = new HttpBodyParser(queryStringParser, multipartParser);

    @Test
    void bodyParsingWithStringTest() {
        //given
        String body = "hello world";

        //when
        HttpBody parse = parser.parse(body);

        //then
        assertEquals(new String(parse.getBody()), body);
    }

    @Test
    void bodyParsingWithByteArrayTest() {
        //given
        byte[] body = "hello world".getBytes();

        //when
        HttpBody parse = parser.parse(body);

        //then
        assertEquals(parse.getBody(), body);
    }

    @Nested
    @DisplayName("parse with header and input stream")
    class ParseWithHeaderAndIS {

        @Test
        void moreThenTwoContentLengthHeadersTest() {
            //given
            HttpHeader header = new HttpHeader();
            header.addHeader("Content-Length", "1");
            header.addHeader("Content-Length", "2");
            header.addHeader("Content-Length", "3");

            //when
            assertThrows(InvalidRequestFormatException.class, () -> {
                parser.parse(header, new ByteArrayInputStream("".getBytes()));
            });
        }

        @Test
        void withMultipartFormDataHeader(){
            //given
            String body = "hello world";
            String contentType = MIME.MULTIPART_FORM_DATA.getMimeType();
            int length = body.getBytes().length;
            HttpHeader header = new HttpHeader();
            header.setHeader("Content-Type",contentType);
            header.setHeader("Content-Length",String.valueOf(length));

            InputStream is = new ByteArrayInputStream(body.getBytes());

            //when
            HttpBody parse = parser.parse(header, is);

            //then
            assertEquals("hyeonuk",parse.getQueryString().get("id"));
            assertEquals(MockHttpMultipartParser.mockFile.getFileName(),parse.getFile().get("file").getFileName());
            assertEquals(MockHttpMultipartParser.mockFile.getData(),parse.getFile().get("file").getData());
        }

        @Test
        void withIOException(){
            //given
            InputStream is = new InputStream() {
                @Override
                public int read() throws IOException {
                    throw new IOException("ioexception");
                }
            };
            HttpHeader header = new HttpHeader();
            header.addHeader("Content-Length", "1");

            //when & then
            assertThrows(InvalidRequestFormatException.class,()->{
                parser.parse(header,is);
            });
        }
    }
}
