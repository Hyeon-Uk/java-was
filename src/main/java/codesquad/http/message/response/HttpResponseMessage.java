package codesquad.http.message.response;
import codesquad.http.message.vo.HttpBody;
import codesquad.http.message.vo.HttpHeader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class HttpResponseMessage {
    private final HttpResponseStartLine startLine;
    private final HttpHeader header;
    private final HttpBody body;

    public HttpResponseMessage(HttpResponseStartLine startLine,HttpHeader header,HttpBody body){
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public HttpResponseMessage(String httpVersion, Map<String, List<String>> header){
        this.startLine = new HttpResponseStartLine(httpVersion);
        this.header = new HttpHeader(header);
        this.body = new HttpBody();
    }
    public void setStatus(HttpStatus status){
        this.startLine.setStatus(status);
    }
    public void setHeader(String key,String value){
        header.setHeader(key,value);
    }
    public void setBody(byte[] body){
        this.body.setBody(body);
    }
    public void setBody(String body){
        setBody(body.getBytes());
    }
    public byte[] getBody(){
        return this.body.getBody();
    }
    private byte[] parseStartLine(){
        return startLine.parseStartLine();
    }
    private byte[] parseHeaders(){
        return header.allHeaders().entrySet().stream()
                .map(entry -> {
                    StringJoiner joiner = new StringJoiner(", ");
                    entry.getValue().forEach(joiner::add);
                    return entry.getKey() + ": " + joiner.toString();
                })
                .reduce("", (acc, line) -> acc + line + System.lineSeparator()).getBytes();
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
    public byte[] parse(){
        byte[] startLine = parseStartLine();
        byte[] headers = parseHeaders();
        byte[] emptyLine = "\r\n".getBytes();
        byte[] body = parseBody();

        return concatByteArray(startLine,headers,emptyLine,body);
    }
}
