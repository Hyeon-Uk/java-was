package codesquad.http.message;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.Map;

public class HttpHeader {
    private final Map<String,String> header;
    public HttpHeader() {
        header = new HashMap<>();
    }
    public HttpHeader(Map<String,String> headerMap) {
        header = headerMap;
    }
    public String getHeader(String key){
        return header.get(key);
    }
    protected void setHeader(String key,String value){
        header.put(key,value);
    }
    protected void setHeader(Map<String,String> header){
        this.header.putAll(header);
    }

    public byte[] parseHeader(){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String,String> header : header.entrySet().stream().sorted((o1,o2)->o1.getKey().compareTo(o2.getKey())).toList()){
            sb.append(header.getKey()).append(':').append(' ').append(header.getValue()).append(System.lineSeparator());
        }

        sb.append(System.lineSeparator());

        return sb.toString().getBytes();
    }
}
