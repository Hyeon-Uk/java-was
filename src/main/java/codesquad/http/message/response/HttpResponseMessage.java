package codesquad.http.message.response;

import codesquad.http.message.HttpHeader;
import codesquad.http.message.InvalidResponseFormatException;
import codesquad.utils.Timer;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class HttpResponseMessage {
    private final HttpResponseStartLine startLine;
    private final HttpHeader header;
    private byte[] body;

    private HttpResponseMessage(Builder builder){
        this(new HttpResponseStartLine(builder.httpVersion,builder.status), new HttpHeader(builder.headerMap),builder.body);
    }

    private HttpResponseMessage(HttpResponseStartLine startLine,HttpHeader header,byte[] body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static class Builder{
        private final HttpStatus status;
        private final Map<String,String> headerMap;
        private final String httpVersion;
        private byte[] body;
        public Builder(HttpStatus status, Map<String,String> headerMap, Timer timer) {
            if(headerMap == null || status == null) throw new InvalidResponseFormatException();

            this.status = status;
            this.headerMap = headerMap;
            this.httpVersion = "HTTP/1.1";

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            this.headerMap.put("Date",dateFormat.format(timer.getCurrentTime()));
        }
        public Builder body(byte[] body) {
            if(body == null){
                body = new byte[0];
            }
            this.body = body;
            this.headerMap.put("Content-Length",Integer.toString(body.length));
            return this;
        }
        public Builder body(String body){
            if(body == null){
                body = "";
            }
            body(body.getBytes());
            return this;
        }
        public HttpResponseMessage build(){
            return new HttpResponseMessage(this);
        }
    }

    public String getHttpVersion() {
        return startLine.getHttpVersion();
    }

    public HttpStatus getStatus() {
        return startLine.getStatus();
    }

    public String getHeader(String header) {
        return this.header.getHeader(header);
    }

    public byte[] getBody() {
        return body;
    }

    public String getBodyString(){
        return new String(body);
    }

    private byte[] parseStartLine(){
        return startLine.parseStartLine();
    }

    private byte[] parseBody(){
        return body;
    }

    private byte[] concatByteArray(byte[] ...args){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if(args != null){
            Arrays.stream(args).filter(Objects::nonNull)
                    .forEach(baos::writeBytes);
        }
        return baos.toByteArray();
    }

    public byte[] parseMessage(){
        byte[] startLineBytes = parseStartLine();
        byte[] headerBytes = header.parseHeader();
        byte[] bodyBytes = parseBody();

        return concatByteArray(startLineBytes,headerBytes,bodyBytes);
    }
}
