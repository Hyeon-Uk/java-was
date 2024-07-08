package codesquad.was.http.message.response;
import codesquad.was.http.message.vo.HttpHeader;
import codesquad.was.http.exception.HttpException;
import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.utils.Timer;

import java.text.SimpleDateFormat;
import java.util.*;

public class HttpResponse {
    private final HttpResponseStartLine startLine;
    private final HttpHeader header;
    private final HttpBody body;
    private final String NEW_LINE = "\r\n";
    public HttpResponse(HttpException httpException){
        this.startLine = new HttpResponseStartLine("HTTP/1.1",httpException.getStatus());
        this.header = new HttpHeader();
        this.body = new HttpBody();
        setBody(httpException.getErrorMessage());
    }
    public HttpResponse(HttpResponseStartLine startLine, HttpHeader header, HttpBody body){
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public HttpResponse(String httpVersion, Map<String, List<String>> header){
        this.startLine = new HttpResponseStartLine(httpVersion);
        this.header = new HttpHeader(header);
        this.body = new HttpBody();
    }
    public void sendRedirect(String redirectPath){
        setHeader("Location",redirectPath);
        setStatus(HttpStatus.FOUND);
    }
    public void setStatus(HttpStatus status){
        this.startLine.setStatus(status);
    }
    public void setHeader(String key,String value){
        header.setHeader(key,value);
    }
    public void setBody(byte[] body){
        this.body.setBody(body);
        this.header.setHeader("Content-Length",String.valueOf(body.length));
    }
    public void setBody(String body){
        setBody(body.getBytes());
    }
    public byte[] getBody(){
        return this.body.getBody();
    }
    public List<String> getHeaders(String key){
        return this.header.getHeaders(key);
    }
    public HttpStatus getStatus(){
        return this.startLine.getStatus();
    }

    private byte[] parseStartLine(){
        StringBuilder sb = new StringBuilder();
        sb.append(startLine.getHttpVersion())
                .append(' ').append(startLine.getStatus().getCode())
                .append(' ').append(startLine.getStatus().getMessage()).append(NEW_LINE);
        return sb.toString().getBytes();
    }
    private byte[] parseHeaders(){
        return header.allHeaders().entrySet().stream()
                .map(entry -> {
                    StringJoiner joiner = new StringJoiner(", ");
                    entry.getValue().forEach(joiner::add);
                    return entry.getKey() + ": " + joiner.toString();
                })
                .reduce("", (acc, line) -> acc + line + NEW_LINE).getBytes();
    }
    private byte[] parseBody(){
        return body.getBody();
    }
    private byte[] mergeByteArray(byte[] array1,byte[] array2){
        byte[] result = new byte[array1.length+array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }
    private byte[] concatByteArray(byte[] ...array){
        return Arrays.stream(array)
                .reduce(new byte[0],this::mergeByteArray);
    }
    public byte[] parse(Timer timer){
        setHeader("Date",getFormattedDate(timer));
        byte[] startLine = parseStartLine();
        byte[] headers = parseHeaders();
        byte[] emptyLine = NEW_LINE.getBytes();
        byte[] body = parseBody();

        return concatByteArray(startLine,headers,emptyLine,body);
    }
    private String getFormattedDate(Timer timer) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        return dateFormat.format(timer.getCurrentTime());
    }
}
