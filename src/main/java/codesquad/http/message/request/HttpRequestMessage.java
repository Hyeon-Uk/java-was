package codesquad.http.message.request;

import codesquad.http.message.InvalidRequestFormatException;
import codesquad.http.message.vo.HttpBody;
import codesquad.http.message.vo.HttpHeader;
import codesquad.http.message.vo.HttpRequestStartLine;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestMessage {
    private final HttpRequestStartLine startLine;
    private final HttpHeader header;
    private final HttpBody body;
    private final Map<String,String> queryString;

    public HttpRequestMessage(HttpRequestStartLine startLine,Map<String,String> queryString, HttpHeader header, HttpBody body) {
        this.startLine = startLine;
        this.queryString = queryString;
        this.header = header;
        this.body = body;
    }

    public String getQueryString(String parameter){
        return queryString.get(parameter);
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getUri() {
        return startLine.getUri();
    }

    public String getHttpVersion() {
        return startLine.getHttpVersion();
    }

    public List<String> getHeader(String key){
        return header.getHeaders(key);
    }

    public byte[] getBody(){
        return body.getBody();
    }
}
