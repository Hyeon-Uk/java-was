package codesquad.was.http.message.parser;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpQueryStringParser {
    public Map<String, String> parse(String queryString) {
        return Arrays.stream(queryString.split("&"))
                .map(query -> query.split("=", 2))
                .collect(Collectors.toMap(
                        keyValue -> keyValue[0],
                        keyValue -> keyValue.length > 1 ? keyValue[1].trim() : ""
                ));
    }
}
