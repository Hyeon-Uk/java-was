package codesquad.was.http.message.parser;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.was.http.exception.HttpInternalServerErrorException;
import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.vo.HttpBody;
import codesquad.was.http.message.vo.HttpFile;
import codesquad.was.http.message.vo.HttpHeader;
import codesquad.was.http.message.vo.MIME;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Coffee
public class HttpBodyParser {
    private final HttpQueryStringParser queryStringParser;
    private final HttpMultipartParser multipartParser;

    public HttpBodyParser(HttpQueryStringParser queryStringParser, HttpMultipartParser multipartParser) {
        this.queryStringParser = queryStringParser;
        this.multipartParser = multipartParser;
    }

    public HttpBody parse(String body){
        return parse(body.getBytes());
    }

    public HttpBody parse(byte[] body){
        return new HttpBody(body);
    }

    public HttpBody parse(HttpHeader header,byte[] body){
        List<String> contentTypes = header.getHeaders("Content-Type");

        if(isFormDataRequest(contentTypes)){
            String bodyString = new String(body);
            return new HttpBody(body,new HashMap<>(),queryStringParser.parse(bodyString));
        }
        if(isMultipartRequest(contentTypes)){
            return multipartParser.parse(header,body);
        }

        return new HttpBody(body);
    }

    private boolean isFormDataRequest(List<String> contentTypes){
        return contentTypes.stream()
                .anyMatch(type->type.contains("application/x-www-form-urlencoded"));
    }
    private boolean isMultipartRequest(List<String> contentTypes){
        return contentTypes.stream()
                .anyMatch(type->type.contains("multipart/form-data"));
    }

    public HttpBody parse(HttpHeader header, InputStream is) {
        try {
            List<String> contentLengthList = header.getHeaders("Content-Length");
            if(contentLengthList.isEmpty()) return parse(header,new byte[0]);
            if(contentLengthList.size() != 1) throw new InvalidRequestFormatException();

            int contentLength = Integer.parseInt(contentLengthList.get(0));
            byte[] body = is.readNBytes(contentLength);
            return parse(header,body);
        } catch (IOException e) {
            throw new InvalidRequestFormatException();
        }
    }
}
