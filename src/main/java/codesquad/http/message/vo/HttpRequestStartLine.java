package codesquad.http.message.vo;

import codesquad.http.message.request.HttpMethod;

public class HttpRequestStartLine {
    private final String httpVersion;
    private final String uri;
    private final HttpMethod method;
    public HttpRequestStartLine(String httpVersion, String uri, HttpMethod method) {
        this.httpVersion = httpVersion;
        this.uri = uri;
        this.method = method;
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


}
