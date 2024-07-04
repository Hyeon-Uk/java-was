package codesquad.http.handler;

import java.util.Map;

public class RequestHandlerMapper {
    private final static String STATIC_RESOURCE_KEY = "staticResourceKey";
    private final static RequestHandler staticResourceHandler = new StaticResourceHandler();
    private static final Map<String, RequestHandler> mappers = Map.of(
            STATIC_RESOURCE_KEY, staticResourceHandler,
            "/", staticResourceHandler
    );

    public RequestHandler getRequestHandler(String path) {
        if (isStaticFileRequest(path)) {
            return mappers.get(STATIC_RESOURCE_KEY);
        }

        return mappers.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(path))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(path.concat(" : request can not found")));
    }

    private boolean isStaticFileRequest(String path) {
        return path.lastIndexOf('.') != -1;
    }
}
