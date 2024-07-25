package codesquad.was.http.message.vo;

import java.util.List;
import java.util.Map;

public class HttpBody {
    private byte[] body;
    private Map<String,HttpFile> file;
    private Map<String,String> queryString;

    public HttpBody(String body) {
        this(body.getBytes());
    }

    public HttpBody(byte[] body){
        this.body = body;
    }

    public HttpBody(byte[] body,Map<String,HttpFile> file,Map<String,String> queryString){
        this.body = body;
        this.file = file;
        this.queryString = queryString;
    }

    public HttpBody() {
        this.body = new byte[0];
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setBody(String body) {
        setBody(body.getBytes());
    }

    public Map<String, HttpFile> getFile() {
        return file;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }
}
