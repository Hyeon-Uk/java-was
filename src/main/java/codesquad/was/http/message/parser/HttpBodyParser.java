package codesquad.was.http.message.parser;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.was.http.message.vo.HttpBody;

@Coffee
public class HttpBodyParser {
    public HttpBody parse(String body){
        return parse(body.getBytes());
    }

    public HttpBody parse(byte[] body){
        return new HttpBody(body);
    }
}
