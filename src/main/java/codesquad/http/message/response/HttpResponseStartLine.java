package codesquad.http.message.response;

import codesquad.http.message.InvalidResponseFormatException;

public class HttpResponseStartLine {
    private final String httpVersion;
    private final HttpStatus status;
    protected HttpResponseStartLine(String httpVersion, HttpStatus status) {
        if(httpVersion == null || status == null) throw new InvalidResponseFormatException();
        this.httpVersion = httpVersion;
        this.status = status;
    }

    protected String getHttpVersion() {
        return this.httpVersion;
    }

    protected HttpStatus getStatus() {
        return this.status;
    }

    protected byte[] parseStartLine(){
        StringBuilder sb = new StringBuilder();
        sb.append(httpVersion).append(' ').append(status.getCode()).append(' ').append(status.getMessage()).append('\n');
        return sb.toString().getBytes();
    }
}
