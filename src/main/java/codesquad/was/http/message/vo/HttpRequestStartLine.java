package codesquad.was.http.message.vo;

import codesquad.was.http.message.request.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestStartLine {
    private final String httpVersion;
    private final String uri;
    private final HttpMethod method;
    private final Map<String,String> queryString;
    public HttpRequestStartLine(String httpVersion, String uri, HttpMethod method) {
        this(httpVersion, uri, method, new HashMap<>());
    }
    public HttpRequestStartLine(String httpVersion, String uri, HttpMethod method, Map<String, String> queryString) {
        this.httpVersion = httpVersion;
        this.uri = uri;
        this.method = method;
        this.queryString = queryString;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getUri() {
        return uri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }
}
