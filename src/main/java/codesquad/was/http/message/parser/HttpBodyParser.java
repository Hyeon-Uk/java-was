package codesquad.was.http.message.parser;

import codesquad.was.http.message.vo.HttpBody;

public class HttpBodyParser {
    public HttpBody parse(String body){
        return parse(body.getBytes());
    }

    public HttpBody parse(byte[] body){
        return new HttpBody(body);
    }
}
