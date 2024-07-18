package codesquad.was.http.message.parser;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.vo.HttpHeader;
import codesquad.was.http.message.vo.HttpRequestStartLine;
import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Coffee
public class HttpRequestParser implements RequestParser{
    private final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);
    private final HttpRequestStartLineParser httpRequestStartLineParser;
    private final HttpHeaderParser httpHeaderParser;
    private final HttpBodyParser httpBodyParser;
    private final SessionManager sessionManager;

    public HttpRequestParser(HttpRequestStartLineParser httpRequestStartLineParser,
                             HttpHeaderParser httpHeaderParser,
                             HttpBodyParser httpBodyParser,
                             SessionManager sessionManager) {
        this.httpRequestStartLineParser = httpRequestStartLineParser;
        this.httpHeaderParser = httpHeaderParser;
        this.httpBodyParser = httpBodyParser;
        this.sessionManager = sessionManager;
    }

    @Override
    public HttpRequest parse(String message){
       return parse(new ByteArrayInputStream(message.getBytes()));
    }

    @Override
    public HttpRequest parse(InputStream is){
        HttpRequestStartLine startLine = httpRequestStartLineParser.parse(is);
        HttpHeader headers = httpHeaderParser.parse(is);
        HttpBody body = httpBodyParser.parse(headers, is);

        Map<String,String> queryString = startLine.getQueryString();
        Map<String, String> bodyQueryString = body.getQueryString();
        if(bodyQueryString != null && !bodyQueryString.isEmpty()){
            queryString.putAll(bodyQueryString);
        }
        return new HttpRequest(startLine,queryString,headers,body,sessionManager);
    }
}
