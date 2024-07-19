package codesquad.was.http.message.parser;

import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.message.vo.HttpFile;
import codesquad.was.http.message.vo.HttpHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HttpMultipartParser 클래스")
class HttpMultipartParserTest {

    @Nested
    @DisplayName("Parse 메소드는")
    class ParseMethod {

        private HttpHeader createHeaderWithContentType(String boundary) {
            String contentTypeHeader = "multipart/form-data; boundary=" + boundary;
            HttpHeader header = new HttpHeader();
            header.addHeader("Content-Type", contentTypeHeader);
            return header;
        }

        private byte[] createRequestBody(String boundary, String bodyContent) {
            String requestBody = "--" + boundary + "\r\n" + bodyContent + "--" + boundary + "--";
            return requestBody.getBytes();
        }

        @Nested
        @DisplayName("정상적인 멀티파트 요청을 받는 경우")
        class WhenValidMultipartRequest {

            @Test
            @DisplayName("멀티파트 요청을 파싱한다")
            void testParse() {
                // given
                String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
                HttpHeader header = createHeaderWithContentType(boundary);

                String bodyContent =
                        "Content-Disposition: form-data; name=\"param1\"\r\n" +
                                "\r\n" +
                                "value1\r\n" +
                                "--" + boundary + "\r\n" +
                                "Content-Disposition: form-data; name=\"file1\"; filename=\"file.txt\"\r\n" +
                                "Content-Type: text/plain\r\n" +
                                "\r\n" +
                                "File content\r\n";

                byte[] bodyBytes = createRequestBody(boundary, bodyContent);
                header.addHeader("Content-Length", String.valueOf(bodyBytes.length));

                // when
                HttpMultipartParser parser = new HttpMultipartParser();
                HttpBody httpBody = parser.parse(header, bodyBytes);

                // then
                assertNotNull(httpBody);
                Map<String, String> params = httpBody.getQueryString();
                Map<String, HttpFile> files = httpBody.getFile();

                assertAll("Multipart request parsing",
                        () -> assertNotNull(params),
                        () -> assertEquals(1, params.size()),
                        () -> assertEquals("value1", params.get("param1"))
                );

                assertAll("File parsing",
                        () -> assertNotNull(files),
                        () -> assertEquals(1, files.size()),
                        () -> {
                            HttpFile file = files.get("file1");
                            assertNotNull(file);
                            assertAll("File properties",
                                    () -> assertEquals("file.txt", file.getFileName()),
                                    () -> assertEquals("text/plain", file.getContentType()),
                                    () -> assertEquals("File content", new String(file.getData()))
                            );
                        }
                );
            }
        }

        @Nested
        @DisplayName("빈 바디를 받는 경우")
        class WhenEmptyBody {

            @Test
            @DisplayName("빈 멀티파트 요청을 파싱한다")
            void testParse_EmptyBody() {
                // given
                String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
                HttpHeader header = createHeaderWithContentType(boundary);
                byte[] bodyBytes = createRequestBody(boundary, "");

                // when
                HttpMultipartParser parser = new HttpMultipartParser();
                HttpBody httpBody = parser.parse(header, bodyBytes);

                // then
                assertNotNull(httpBody);
                Map<String, String> params = httpBody.getQueryString();
                Map<String, HttpFile> files = httpBody.getFile();

                assertAll("Empty body parsing",
                        () -> assertNotNull(params),
                        () -> assertEquals(0, params.size()),
                        () -> assertNotNull(files),
                        () -> assertEquals(0, files.size())
                );
            }
        }

        @Nested
        @DisplayName("하나의 파라미터만 있는 경우")
        class WhenSingleParameterOnly {

            @Test
            @DisplayName("하나의 파라미터를 파싱한다")
            void testParse_SingleParameterOnly() {
                // given
                String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
                HttpHeader header = createHeaderWithContentType(boundary);

                String bodyContent =
                        "Content-Disposition: form-data; name=\"param1\"\r\n" +
                                "\r\n" +
                                "value1\r\n";

                byte[] bodyBytes = createRequestBody(boundary, bodyContent);

                // when
                HttpMultipartParser parser = new HttpMultipartParser();
                HttpBody httpBody = parser.parse(header, bodyBytes);

                // then
                assertNotNull(httpBody);
                Map<String, String> params = httpBody.getQueryString();
                Map<String, HttpFile> files = httpBody.getFile();

                assertAll("Single parameter parsing",
                        () -> assertNotNull(params),
                        () -> assertEquals(1, params.size()),
                        () -> assertEquals("value1", params.get("param1")),
                        () -> assertNotNull(files),
                        () -> assertEquals(0, files.size())
                );
            }
        }

        @Nested
        @DisplayName("마지막 파트만 있는 경우")
        class WhenLastPartOnly {

            @Test
            @DisplayName("마지막 파트를 파싱한다")
            void testParse_LastPartOnly() {
                // given
                String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
                HttpHeader header = createHeaderWithContentType(boundary);

                String bodyContent =
                        "Content-Disposition: form-data; name=\"param1\"\r\n" +
                                "\r\n" +
                                "value1\r\n" +
                                "--" + boundary + "--\r\n";

                byte[] bodyBytes = createRequestBody(boundary, bodyContent);

                // when
                HttpMultipartParser parser = new HttpMultipartParser();
                HttpBody httpBody = parser.parse(header, bodyBytes);

                // then
                assertNotNull(httpBody);
                Map<String, String> params = httpBody.getQueryString();
                Map<String, HttpFile> files = httpBody.getFile();

                assertAll("Last part parsing",
                        () -> assertNotNull(params),
                        () -> assertEquals(1, params.size()),
                        () -> assertEquals("value1", params.get("param1")),
                        () -> assertNotNull(files),
                        () -> assertEquals(0, files.size())
                );
            }
        }
    }
}
