package codesquad.http.message.response;
import codesquad.http.message.vo.HttpBody;
import codesquad.http.message.vo.HttpHeader;

import java.util.Arrays;
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

    private byte[] parseStartLine(){
        return startLine.parseStartLine();
    }
    private byte[] parseHeaders(){
        return header.allHeaders().entrySet().stream()
                .map(entry -> {
                    StringJoiner joiner = new StringJoiner(", ");
                    entry.getValue().forEach(value -> joiner.add(value));
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
        byte[] body = parseBody();

        return concatByteArray(startLine,headers,body);
    }
}
