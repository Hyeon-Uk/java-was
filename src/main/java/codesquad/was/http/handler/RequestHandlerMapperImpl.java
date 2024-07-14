package codesquad.was.http.handler;

import codesquad.application.handler.*;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.exception.HttpNotFoundException;

import java.util.Map;

@Coffee
public class RequestHandlerMapperImpl implements RequestHandlerMapper{
    private final static String STATIC_RESOURCE_KEY = "staticResourceKey";
    private final static RequestHandler staticResourceHandler = new StaticResourceHandler();
    private final Map<String, RequestHandler> mappers;
    private final UserDatabase userDatabase;
    public RequestHandlerMapperImpl(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
        mappers = Map.of(
                STATIC_RESOURCE_KEY, staticResourceHandler,
                "/user/create",new RegisterHandler(userDatabase),
                "/login",new LoginHandler(userDatabase),
                "/logout",new LogoutHandler(),
                "/user/list",new UserListHandler(userDatabase),
                "/main",new MainHandler(),
                "/registration",new RegisterHandler(userDatabase)
        );
    }

    public RequestHandler getRequestHandler(String path) {
        if("/".equals(path)){
            path="/main";
        }
        if (isStaticFileRequest(path)) {
            return mappers.get(STATIC_RESOURCE_KEY);
        }

        String finalPath = path;
        return mappers.entrySet()
                .stream()
                .sorted((o1,o2)->Integer.compare(o2.getKey().length(),o1.getKey().length()))
                .filter(entry -> finalPath.startsWith(entry.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new HttpNotFoundException(finalPath.concat(" : request can not found")));
    }

    private boolean isStaticFileRequest(String path) {
        return path.lastIndexOf('.') != -1;
    }
}
