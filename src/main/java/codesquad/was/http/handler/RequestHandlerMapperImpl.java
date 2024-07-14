package codesquad.was.http.handler;

import codesquad.application.handler.*;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.Named;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.exception.HttpNotFoundException;
import codesquad.was.utils.FileUtil;

import java.util.HashMap;
import java.util.Map;

@Coffee
public class RequestHandlerMapperImpl implements RequestHandlerMapper{
    private final static String STATIC_RESOURCE_KEY = "staticResourceKey";
    private final RequestHandler staticResourceHandler;
    private final DefaultHandler defaultHandler;
    private final Map<String, RequestHandler> mappers;
    private final UserDatabase userDatabase;
    private final FileUtil fileUtil;
    public RequestHandlerMapperImpl(FileUtil fileUtil,
                                    UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
        this.fileUtil = fileUtil;
        this.staticResourceHandler = new StaticResourceHandler(fileUtil);
        this.defaultHandler = new DefaultHandler(fileUtil);
//        mappers = Map.of(
//                STATIC_RESOURCE_KEY, staticResourceHandler,
//                "/user/create",new RegisterHandler(userDatabase),
//                "/login",new LoginHandler(userDatabase),
//                "/logout",new LogoutHandler(),
//                "/user/list",new UserListHandler(userDatabase),
//                "/",new MainHandler(fileUtil),
//                "/registration",new RegisterHandler(userDatabase)
//        );
        mappers = new HashMap<>();
        setRequestHandler(STATIC_RESOURCE_KEY,staticResourceHandler);
    }

    @Override
    public void setRequestHandler(String path,RequestHandler requestHandler) {
        mappers.put(path,requestHandler);
    }

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
//                .orElseThrow(() -> new HttpNotFoundException(path.concat(" : request can not found")));
                .orElse(defaultHandler);
    }

    private boolean isStaticFileRequest(String path) {
        return fileUtil.isFilePath(path);
    }
}
