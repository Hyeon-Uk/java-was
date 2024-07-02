package codesquad.message.request;

import codesquad.message.InvalidRequestFormatException;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestMessage {
    private HttpMethod method;
    private String uri;
    private String httpVersion;
    private String host;
    private final Map<String,String> headers = new HashMap<>();

    public HttpRequestMessage(String message) throws InvalidRequestFormatException {
        try {
            String[] lines = message.split(System.lineSeparator());
            String startLine = lines[0];
            setStartLine(startLine);
            setHost(lines[1].split(":",2)[1]);
            for(int lineNumber = 2; lineNumber < lines.length; lineNumber++) {
                String line = lines[lineNumber];
                String[] strs = line.split(":");
                headers.put(strs[0].trim(),strs[1].trim());
            }
        }catch(Exception e){
            throw new InvalidRequestFormatException();
        }
    }

    private void setStartLine(String startLine){
        String[] strs = startLine.split(" ");
        setMethod(strs[0]);
        setUri(strs[1]);
        setHttpVersion(strs[2]);
    }

    private void setMethod(String method) {
        this.method = HttpMethod.from(method);
    }

    private void setUri(String uri) {
        this.uri = uri.trim();
    }

    private void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion.trim();
    }

    private void setHost(String host) {
        this.host = host.trim();
    }


    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getHost() {
        return host;
    }

    public String getHeader(String key){
        return headers.get(key);
    }
}
