package codesquad.was.http.message.parser;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.message.vo.HttpHeader;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Coffee
public class HttpBodyParser {
    private final HttpQueryStringParser queryStringParser;
    public HttpBodyParser(HttpQueryStringParser queryStringParser) {
        this.queryStringParser = queryStringParser;
    }
    public HttpBody parse(String body){
        return parse(body.getBytes());
    }

    public HttpBody parse(byte[] body){
        return new HttpBody(body);
    }

    public HttpBody parse(HttpHeader header, InputStream is) {
        try {
            List<String> contentLengthList = header.getHeaders("Content-Length");
            if(contentLengthList.isEmpty()) return parse(new byte[0]);
            if(contentLengthList.size() != 1) throw new InvalidRequestFormatException();

            int contentLength = Integer.parseInt(contentLengthList.get(0));
            byte[] body = is.readNBytes(contentLength);
            return parse(body);
        } catch (IOException e) {
            throw new InvalidRequestFormatException();
        }
    }
}
