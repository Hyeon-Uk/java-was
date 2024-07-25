package codesquad.was.http.message.parser.mock;

import codesquad.was.http.message.parser.HttpMultipartParser;
import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.message.vo.HttpFile;
import codesquad.was.http.message.vo.HttpHeader;

import java.util.Map;

public class MockHttpMultipartParser extends HttpMultipartParser {
    public final static String fileKey = "file";
    public final static HttpFile mockFile = new HttpFile("text/html", "mockfile.html", new byte[0]);
    public final static Map<String, HttpFile> multipartFileMap = Map.of(fileKey, mockFile);
    public final static Map<String, String> multipartQueryString = Map.of("id", "hyeonuk");

    @Override
    public HttpBody parse(HttpHeader header, byte[] bodyBytes) {
        return new HttpBody(bodyBytes, multipartFileMap, multipartQueryString);
    }
}
