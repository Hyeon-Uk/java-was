package codesquad.was.http.message.parser;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.was.http.message.InvalidRequestFormatException;
import codesquad.was.http.message.vo.HttpHeader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;


@Coffee
public class HttpHeaderParser {
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
