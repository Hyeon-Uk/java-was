package codesquad.was.http.message.vo;

import java.util.*;

public class HttpHeader {
    private final Map<String, List<String>> header;

    public HttpHeader() {
        this(new HashMap<>());
    }

    public HttpHeader(Map<String, List<String>> header) {
        this.header = header;
    }

    public List<String> getHeaders(String key) {
        return header.getOrDefault(key, new ArrayList<>());
    }

    public Map<String, List<String>> allHeaders() {
        return this.header;
    }

    public void setHeader(String key, String value) {
        header.put(key, Arrays.stream(value.trim().split(","))
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .toList());
    }

    public void addHeader(String key, String value) {
        header.computeIfAbsent(key, k -> new ArrayList<>())
                .add(value);
    }
}
