package codesquad.http.message.vo;

public class HttpBody {
    private byte[] body;

    public HttpBody(String body) {
        this(body.getBytes());
    }

    public HttpBody(byte[] body) {
        this.body = body;
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
}
