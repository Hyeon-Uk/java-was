package codesquad.was.http.handler;

import codesquad.application.handler.LoginHandler;
import codesquad.application.handler.MainHandler;
import codesquad.application.handler.RegisterHandler;
import codesquad.was.http.exception.HttpNotFoundException;

import java.util.Map;

public class RequestHandlerMapperImpl implements RequestHandlerMapper{
    private final static String STATIC_RESOURCE_KEY = "staticResourceKey";
    private final static RequestHandler staticResourceHandler = new StaticResourceHandler();
    private static final Map<String, RequestHandler> mappers = Map.of(
            STATIC_RESOURCE_KEY, staticResourceHandler,
            "/user/create",new RegisterHandler(),
            "/login",new LoginHandler(),
            "/", new MainHandler()
    );

    public RequestHandler getRequestHandler(String path) {
        if (isStaticFileRequest(path)) {
            return mappers.get(STATIC_RESOURCE_KEY);
        }

        return mappers.entrySet()
                .stream()
                .filter(entry -> path.startsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow(() -> new HttpNotFoundException(path.concat(" : request can not found")));
    }

    private boolean isStaticFileRequest(String path) {
        return path.lastIndexOf('.') != -1;
    }
}
