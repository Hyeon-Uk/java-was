package codesquad.was.http.message.parser;

import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.message.vo.HttpFile;
import codesquad.was.http.message.vo.HttpHeader;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpMultipartParserTest {
    @Test
    public void testParse() {
        // given
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        String contentTypeHeader = "multipart/form-data; boundary=" + boundary;
        HttpHeader header = new HttpHeader();
        header.addHeader("Content-Type", contentTypeHeader);

        String requestBody =
                "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"param1\"\r\n" +
                "\r\n" +
                "value1\r\n" +
                "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"file1\"; filename=\"file.txt\"\r\n" +
                "Content-Type: text/plain\r\n" +
                "\r\n" +
                "File content\r\n" +
                "--" + boundary + "--";

        byte[] bodyBytes = requestBody.getBytes();
        header.addHeader("Content-Length", String.valueOf(bodyBytes.length));

        // when
        HttpMultipartParser parser = new HttpMultipartParser();
        HttpBody httpBody = parser.parse(header, bodyBytes);

        //then
        assertNotNull(httpBody);
        Map<String, String> params = httpBody.getQueryString();
        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals("value1", params.get("param1"));

        Map<String, HttpFile> files = httpBody.getFile();
        assertNotNull(files);
        assertEquals(1, files.size());
        HttpFile file = files.get("file1");
        assertNotNull(file);
        assertEquals("file.txt", file.getFileName());
        assertEquals("text/plain", file.getContentType());
        assertEquals("File content", new String(file.getData()));
    }

    @Test
    public void testParse_EmptyBody() {
        // given
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        String contentTypeHeader = "multipart/form-data; boundary=" + boundary;
        HttpHeader header = new HttpHeader();
        header.addHeader("Content-Type", contentTypeHeader);

        String requestBody = "";

        byte[] bodyBytes = requestBody.getBytes();

        // when
        HttpMultipartParser parser = new HttpMultipartParser();
        HttpBody httpBody = parser.parse(header, bodyBytes);

        //then
        assertNotNull(httpBody);
        Map<String, String> params = httpBody.getQueryString();
        assertNotNull(params);
        assertEquals(0, params.size());

        Map<String, HttpFile> files = httpBody.getFile();
        assertNotNull(files);
        assertEquals(0, files.size());
    }

    @Test
    public void testParse_SingleParameterOnly() {
        // given
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        String contentTypeHeader = "multipart/form-data; boundary=" + boundary;
        HttpHeader header = new HttpHeader();
        header.addHeader("Content-Type", contentTypeHeader);

        String requestBody = "" +
                "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"param1\"\r\n" +
                "\r\n" +
                "value1\r\n" +
                "--" + boundary + "--";

        byte[] bodyBytes = requestBody.getBytes();

        // when
        HttpMultipartParser parser = new HttpMultipartParser();
        HttpBody httpBody = parser.parse(header, bodyBytes);

        //then
        assertNotNull(httpBody);
        Map<String, String> params = httpBody.getQueryString();
        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals("value1", params.get("param1"));

        Map<String, HttpFile> files = httpBody.getFile();
        assertNotNull(files);
        assertEquals(0, files.size());
    }

    @Test
    public void testParse_LastPartOnly() {
        // given
        String boundary = "----WebKitFormBoundary7MA4YWxkTrZu0gW";
        String contentTypeHeader = "multipart/form-data; boundary=" + boundary;
        HttpHeader header = new HttpHeader();
        header.addHeader("Content-Type", contentTypeHeader);

        String requestBody = "" +
                "--" + boundary + "\r\n" +
                "Content-Disposition: form-data; name=\"param1\"\r\n" +
                "\r\n" +
                "value1\r\n" +
                "--" + boundary + "--\r\n";

        byte[] bodyBytes = requestBody.getBytes();

        // when
        HttpMultipartParser parser = new HttpMultipartParser();
        HttpBody httpBody = parser.parse(header, bodyBytes);

        //then
        assertNotNull(httpBody);
        Map<String, String> params = httpBody.getQueryString();
        assertNotNull(params);
        assertEquals(1, params.size());
        assertEquals("value1", params.get("param1"));

        Map<String, HttpFile> files = httpBody.getFile();
        assertNotNull(files);
        assertEquals(0, files.size());
    }
}