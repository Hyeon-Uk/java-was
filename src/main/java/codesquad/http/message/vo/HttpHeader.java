package codesquad.http.message.vo;

import java.net.http.HttpHeaders;
import java.util.*;

public class HttpHeader {
    private final Map<String, List<String>> header;

    public HttpHeader(){
        this(new HashMap<>());
    }
    public HttpHeader(Map<String,List<String>> header){
        this.header = header;
    }

    public List<String> getHeaders(String key){
        return header.getOrDefault(key,new ArrayList<>());
    }
    public Map<String,List<String>> allHeaders(){
        return this.header;
    }
    public void setHeader(String key,String value){
        List<String> values = header.getOrDefault(key,new ArrayList<>());
        values.add(value);
        header.put(key,values);
    }
}
