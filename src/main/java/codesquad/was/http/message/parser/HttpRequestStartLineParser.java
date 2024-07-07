package codesquad.was.http.message.parser;

import codesquad.was.http.message.vo.HttpRequestStartLine;
import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.request.HttpMethod;

import java.net.URLDecoder;
import java.util.Optional;

public class HttpRequestStartLineParser {
    public HttpRequestStartLine parse(String startLine){
        String[] startLineComponents = startLine.split(" ");
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
