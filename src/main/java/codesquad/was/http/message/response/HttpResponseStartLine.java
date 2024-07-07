package codesquad.was.http.message.response;

import codesquad.was.http.message.InvalidResponseFormatException;

public class HttpResponseStartLine {
    private final String httpVersion;
    private HttpStatus status;

    protected HttpResponseStartLine(String httpVersion, HttpStatus status) {
        if (httpVersion == null || status == null) throw new InvalidResponseFormatException();
        this.httpVersion = httpVersion;
        this.status = status;
    }

    protected HttpResponseStartLine(String httpVersion) {
        this.httpVersion = httpVersion;

    }

    protected String getHttpVersion() {
        return this.httpVersion;
    }

    protected HttpStatus getStatus() {
        return this.status;
    }

    protected void setStatus(HttpStatus status) {
        this.status = status;
    }

    protected boolean isValidated() {
        return this.httpVersion != null && this.status != null;
    }

    protected byte[] parseStartLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(httpVersion).append(' ').append(status.getCode()).append(' ').append(status.getMessage()).append("\r\n");
        return sb.toString().getBytes();
    }
}
