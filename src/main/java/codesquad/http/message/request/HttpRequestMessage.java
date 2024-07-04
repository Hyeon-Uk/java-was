package codesquad.http.message.request;

import codesquad.http.message.InvalidRequestFormatException;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestMessage {
    private HttpMethod method;
    private String uri;
    private String httpVersion;
    private String host;
    private final Map<String,String> headers = new HashMap<>();
    private final Map<String,String> parameters = new HashMap<>();

    public HttpRequestMessage(String message) throws InvalidRequestFormatException {
        try {
            String[] lines = message.split(System.lineSeparator());
            String startLine = lines[0];
            setStartLine(startLine);
            setHost(lines[1].split(":",2)[1]);
            for(int lineNumber = 2; lineNumber < lines.length && !"".equals(lines[lineNumber].trim()); lineNumber++) {
                String line = lines[lineNumber];
                String[] strs = line.split(":");
                headers.put(strs[0].trim(),strs[1].trim());
            }
        }catch(Exception e){
            e.printStackTrace();
            throw new InvalidRequestFormatException();
        }
    }

    private void setStartLine(String startLine){
        String[] strs = startLine.split(" ");
        setMethod(strs[0]);
        setUri(strs[1]);
        setQueryString(uri);
        setHttpVersion(strs[2]);
    }

    private Map<String,String> extractQueryString(String queryString){
        Map<String,String> queryStringMap = new HashMap<>();
        String[] queries = queryString.split("&");
        for(String query : queries){
            String[] keyValue = Arrays.stream(query.split("="))
                    .map(URLDecoder::decode)
                    .toArray(String[]::new);

            if(keyValue.length == 1){
                queryStringMap.put(keyValue[0].trim(),"");
            }
            else if(keyValue.length == 2){
                queryStringMap.put(keyValue[0].trim(),keyValue[1].trim());
            }
        }
        return queryStringMap;
    }

    private void setQueryString(String uri){
        String[] split = uri.split("\\?");
        if(split.length <= 1) return;
        String queryStrings = split[1];
        Map<String, String> queryStringMap = extractQueryString(queryStrings);
        this.parameters.putAll(queryStringMap);
    }

    public String getQueryString(String parameter){
        return parameters.get(parameter);
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
