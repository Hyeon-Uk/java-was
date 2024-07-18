package codesquad.was.http.message.parser;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.vo.HttpHeader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;


@Coffee
public class HttpHeaderParser {
    public HttpHeader parse(InputStream is){
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] read = new byte[1];
            Queue<Byte> q = new LinkedList<>();
            while (is.read(read) != -1) {
                bos.write(read[0]);
                q.offer(read[0]);
                if(q.size() > 4){
                    q.poll();
                }

                if(isEndOfHeader(q)){
                    break;
                }
            }
            byte[] byteArray = bos.toByteArray();
            StringBuilder sb = new StringBuilder();
            for(byte b : byteArray){
                sb.append((char)b);
            }
            String[] headerLines = sb.toString().trim().split("\r\n");
            return parse(headerLines);
        }catch(IOException e) {
            throw new InvalidRequestFormatException();
        }
    }

    private boolean isEndOfHeader(Queue<Byte> q){
        byte[] endOfHeaderBytes = {'\r','\n','\r','\n'};
        int index = 0;
        Iterator<Byte> it = q.iterator();
        while(it.hasNext()){
            Byte b = it.next();
            if(b != endOfHeaderBytes[index++]){
                return false;
            }
        }
        return true;
    }

    public HttpHeader parse(String[] headers) {
        return new HttpHeader(Arrays.stream(headers)
                .map(this::getStringStringEntry)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ))
        );
    }

    private Map.Entry<String, List<String>> getStringStringEntry(String header) {
        String[] keyValue = header.split(":", 2);
        if (keyValue.length != 2) throw new InvalidRequestFormatException();
        return Map.entry(keyValue[0].trim(),
                Arrays.stream(keyValue[1].trim().split(","))
                        .map(String::trim)
                        .filter(value -> !value.isEmpty())
                        .toList()
        );
    }
}
