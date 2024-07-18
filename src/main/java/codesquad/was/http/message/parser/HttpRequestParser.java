package codesquad.was.http.message.parser;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.vo.HttpHeader;
import codesquad.was.http.message.vo.HttpRequestStartLine;
import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.session.SessionManager;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Coffee
public class HttpRequestParser implements RequestParser{
    private final HttpRequestStartLineParser httpRequestStartLineParser;
    private final HttpHeaderParser httpHeaderParser;
    private final HttpBodyParser httpBodyParser;
    private final HttpQueryStringParser httpQueryStringParser;
    private final SessionManager sessionManager;
    private final String CRLF = "\r\n";

    public HttpRequestParser(HttpRequestStartLineParser httpRequestStartLineParser,
                             HttpHeaderParser httpHeaderParser,
                             HttpBodyParser httpBodyParser,
                             HttpQueryStringParser httpQueryStringParser,
                             SessionManager sessionManager) {
        this.httpRequestStartLineParser = httpRequestStartLineParser;
        this.httpHeaderParser = httpHeaderParser;
        this.httpBodyParser = httpBodyParser;
        this.httpQueryStringParser = httpQueryStringParser;
        this.sessionManager = sessionManager;
    }

    @Override
    public HttpRequest parse(String message){
        String[] parts = message.split(CRLF+CRLF,2);

        String headerPart = parts[0];
        String bodyPart = parts.length == 2 ? parts[1] : "";

        //header&startline parse
        String[] headerLines = headerPart.split(CRLF);
        if(headerLines.length < 1) throw new InvalidRequestFormatException();
        String startLine = headerLines[0];
        HttpRequestStartLine httpRequestStartLine = httpRequestStartLineParser.parse(startLine);

        String uriWithQueryString = startLine.split(" ")[1];
        Map<String,String> queryString = httpQueryStringParser.parse(URLDecoder.decode(uriWithQueryString.substring(uriWithQueryString.indexOf('?')+1)));
        String[] headers = Arrays.copyOfRange(headerLines,1,headerLines.length);
        HttpHeader httpHeader = httpHeaderParser.parse(headers);

        //body parse
        HttpBody httpBody = httpBodyParser.parse(bodyPart);
        Map<String, String> bodyQueryString = httpQueryStringParser.parse(URLDecoder.decode(bodyPart));
        queryString.putAll(bodyQueryString);

        return new HttpRequest(httpRequestStartLine,queryString,httpHeader,httpBody,sessionManager);
    }

    @Override
    public HttpRequest parse(InputStream is){
        HttpRequestStartLine startLine = httpRequestStartLineParser.parse(is);
        HttpHeader headers = httpHeaderParser.parse(is);
        HttpBody body = httpBodyParser.parse(headers, is);

        Map<String,String> queryString = startLine.getQueryString();
        String bodyPart = new String(body.getBody());
        queryString.putAll(httpQueryStringParser.parse(URLDecoder.decode(bodyPart)));

        return new HttpRequest(startLine,queryString,headers,body,sessionManager);
    }
}
