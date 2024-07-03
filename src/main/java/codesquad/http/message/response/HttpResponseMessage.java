package codesquad.http.message.response;

import codesquad.http.message.InvalidResponseFormatException;
import codesquad.utils.Timer;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

public class HttpResponseMessage {
    private String httpVersion;
    private HttpStatus status;
    private final Map<String,String> header = new HashMap<>();
    private byte[] body;

    private HttpResponseMessage() {
        this.httpVersion = "HTTP/1.1";
    }

    public static class Builder{
        private HttpResponseMessage responseMessage;
        private Timer timer;
        public Builder(Timer timer){
            responseMessage = new HttpResponseMessage();
            this.timer = timer;
        }
        public Builder status(HttpStatus status){
            responseMessage.status = status;
            return this;
        }
        public Builder header(String key,String value){
            responseMessage.header.put(key, value);
            return this;
        }
        public Builder headers(Map<String,String> headers){
            responseMessage.header.putAll(headers);
            return this;
        }
        public Builder body(String body){
            if(body == null){
                body = "";
            }
            responseMessage.header.put("Content-Length", Integer.toString(body.getBytes().length));
            responseMessage.body = body.getBytes();
            return this;
        }
        public Builder body(byte[] body){
            if(body == null){
                body = new byte[0];
            }
            responseMessage.header.put("Content-Length", Integer.toString(body.length));
            responseMessage.body = body;
            return this;
        }

        public HttpResponseMessage build() throws InvalidResponseFormatException {
            validation();

            // SimpleDateFormat을 사용하여 HTTP date 포맷을 만듭니다.
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            responseMessage.header.put("Date",dateFormat.format(timer.getCurrentTime()));
            return responseMessage;
        }
        private void validation() throws InvalidResponseFormatException {
            if( !(this.timer != null && this.responseMessage.getStatus() != null)) throw new InvalidResponseFormatException();
        }
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getHeader(String header) {
        return this.header.get(header);
    }

    public byte[] getBody() {
        return body;
    }

    private byte[] parseHeader(){
        StringBuilder sb = new StringBuilder()
                .append(httpVersion).append(' ').append(status.getCode()).append(' ').append(status.getMessage()).append(System.lineSeparator());

        for(Map.Entry<String,String> header : header.entrySet().stream().sorted((o1,o2)->o1.getKey().compareTo(o2.getKey())).toList()){
            sb.append(header.getKey()).append(':').append(' ').append(header.getValue()).append(System.lineSeparator());
        }

        sb.append(System.lineSeparator());

        return sb.toString().getBytes();
    }
    private byte[] parseBody(){
        return body;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder()
                .append(httpVersion).append(' ').append(status.getCode()).append(' ').append(status.getMessage()).append(System.lineSeparator());

        for(Map.Entry<String,String> header : header.entrySet().stream().sorted((o1,o2)->o1.getKey().compareTo(o2.getKey())).toList()){
            sb.append(header.getKey()).append(':').append(' ').append(header.getValue()).append(System.lineSeparator());
        }

        sb.append(System.lineSeparator());
        sb.append(body).append(System.lineSeparator());

        return sb.toString();
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
        byte[] headerBytes = parseHeader();
        byte[] bodyBytes = parseBody();

        return concatByteArray(headerBytes,bodyBytes);
    }
}
