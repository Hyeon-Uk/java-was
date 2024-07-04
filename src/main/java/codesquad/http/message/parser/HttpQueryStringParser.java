package codesquad.http.message.parser;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpQueryStringParser {
    public Map<String, String> parse(String uri) {
        return Arrays.stream(uri.split("\\?", 2))
                .skip(1)
                .flatMap(queryString -> Arrays.stream(queryString.split("&")))
                .map(query -> query.split("=", 2))
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue.length > 1 ? keyValue[1] : ""
                ));
    }
}
