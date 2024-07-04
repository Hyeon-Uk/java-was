package codesquad.http.message.vo;

public class HttpBody {
    private final byte[] body;
    public HttpBody(String body){
        this(body.getBytes());
    }
    public HttpBody(byte[] body){
        this.body = body;
    }
    public byte[] getBody(){
        return body;
    }
}
