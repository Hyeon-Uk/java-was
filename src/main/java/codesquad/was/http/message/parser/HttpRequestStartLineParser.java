package codesquad.was.http.message.parser;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.was.http.message.vo.HttpRequestStartLine;
import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.request.HttpMethod;

import java.io.*;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Optional;

@Coffee
public class HttpRequestStartLineParser {
    public HttpRequestStartLine parse(InputStream is) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] read = new byte[1];
            byte last = -1;
            while (is.read(read) != -1) {
                if (isNewLine(last, read[0])) break;
                bos.write(read);
                last = read[0];
            }
            byte[] byteArray = bos.toByteArray();
            StringBuilder sb = new StringBuilder();
            for(byte b : byteArray){
                if((char)b == '\n' || (char)b =='\r') {
                    break;
                }
                sb.append((char)b);
            }
            String startLineString = sb.toString();
            return parse(startLineString);
        }catch(IOException e) {
            throw new InvalidRequestFormatException();
        }
    }

    private boolean isNewLine(byte tail1,byte tail2){
        return (char)tail1 == '\r' && (char)tail2 == '\n';
    }

    public HttpRequestStartLine parse(String startLine){
        String[] startLineComponents = Arrays.stream(startLine.split(" "))
                .map(String::trim)
                .filter(component -> !"".equals(component))
                .toArray(String[]::new);
        if(startLineComponents.length != 3) throw new InvalidRequestFormatException();
        HttpMethod method = extractMethod(startLineComponents[0]);
        String uri = extractUri(startLineComponents[1]);
        String httpVersion = extractHttpVersion(startLineComponents[2]);

        return new HttpRequestStartLine(httpVersion,uri,method);
    }

    //TODO 추후 extract에서 request startline의 형식이 맞는지 검증하는 코드를 extract 메서드 내에서 검증
    //TODO URLDecoder의 .decode(String) 은 deprecated됨, nio 패키지에 charset이 존재하기 때문에, 이를 사용하지 않고 직접 구현하기
    private String extractUri(String uri) {
        String decoded = URLDecoder.decode(uri);
        int index = decoded.indexOf('?');

        // Extract the URI without the query string
        String uriWithoutQuery = (index == -1) ? decoded : decoded.substring(0, index);
        return uriWithoutQuery;
    }
    
    private String extractHttpVersion(String httpVersion){
        return httpVersion;
    }

    private HttpMethod extractMethod(String method){
        return Optional.ofNullable(HttpMethod.from(method))
                .orElseThrow(InvalidRequestFormatException::new);
    }

}
