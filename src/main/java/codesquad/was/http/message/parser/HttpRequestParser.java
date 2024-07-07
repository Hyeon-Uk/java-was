package codesquad.was.http.message.parser;

import codesquad.was.http.message.vo.HttpHeader;
import codesquad.was.http.message.vo.HttpRequestStartLine;
import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.vo.HttpBody;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;

public class HttpRequestParser {
    private final HttpRequestStartLineParser httpRequestStartLineParser;
    private final HttpHeaderParser httpHeaderParser;
    private final HttpBodyParser httpBodyParser;
    private final HttpQueryStringParser httpQueryStringParser;
    private final String CRLF = System.lineSeparator();

    public HttpRequestParser(HttpRequestStartLineParser httpRequestStartLineParser, HttpHeaderParser httpHeaderParser, HttpBodyParser httpBodyParser,HttpQueryStringParser httpQueryStringParser) {
        this.httpRequestStartLineParser = httpRequestStartLineParser;
        this.httpHeaderParser = httpHeaderParser;
        this.httpBodyParser = httpBodyParser;
        this.httpQueryStringParser = httpQueryStringParser;
    }

    public HttpRequest parse(String message){
        String[] parts = message.split(CRLF+CRLF,2);

        String headerPart = parts[0];
        String bodyPart = parts.length == 2 ? parts[1] : "";

        //header&startline parse
        String[] headerLines = headerPart.split(CRLF);
        if(headerLines.length < 1) throw new InvalidRequestFormatException();
        String startLine = headerLines[0];
        HttpRequestStartLine httpRequestStartLine = httpRequestStartLineParser.parse(startLine);

        Map<String,String> queryString = httpQueryStringParser.parse(URLDecoder.decode(startLine.split(" ")[1]));

        String[] headers = Arrays.copyOfRange(headerLines,1,headerLines.length);
        HttpHeader httpHeader = httpHeaderParser.parse(headers);

        //body parse
        HttpBody httpBody = httpBodyParser.parse(bodyPart);

        return new HttpRequest(httpRequestStartLine,queryString,httpHeader,httpBody);
    }
}
