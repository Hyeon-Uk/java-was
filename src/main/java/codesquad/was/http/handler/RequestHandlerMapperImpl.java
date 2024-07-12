package codesquad.was.http.handler;

import codesquad.application.handler.*;
import codesquad.was.http.exception.HttpNotFoundException;

import java.util.Map;

public class RequestHandlerMapperImpl implements RequestHandlerMapper{
    private final static String STATIC_RESOURCE_KEY = "staticResourceKey";
    private final static RequestHandler staticResourceHandler = new StaticResourceHandler();
    private static final Map<String, RequestHandler> mappers = Map.of(
            STATIC_RESOURCE_KEY, staticResourceHandler,
            "/user/create",new RegisterHandler(),
            "/login",new LoginHandler(),
            "/logout",new LogoutHandler(),
            "/user/list",new UserListHandler(),
            "/main",new MainHandler(),
            "/", new MainHandler()
    );

    public RequestHandler getRequestHandler(String path) {
        if (isStaticFileRequest(path)) {
            return mappers.get(STATIC_RESOURCE_KEY);
        }

        return mappers.entrySet()
                .stream()
                .sorted((o1,o2)->Integer.compare(o2.getKey().length(),o1.getKey().length()))
                .filter(entry -> path.equals(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new HttpNotFoundException(path.concat(" : request can not found")));
    }

    private boolean isStaticFileRequest(String path) {
        return path.lastIndexOf('.') != -1;
    }
}
