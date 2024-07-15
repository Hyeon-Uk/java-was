package codesquad.was.http.handler;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.Named;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.exception.HttpMethodNotAllowedException;
import codesquad.was.http.exception.HttpNotFoundException;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.utils.FileUtil;

import java.util.HashMap;
import java.util.Map;

@Coffee
public class RequestHandlerMapperImpl implements RequestHandlerMapper{
    private final static String STATIC_RESOURCE_KEY = "staticResourceKey";
    private final Map<String, Map<HttpMethod,RequestHandler>> mappers;
    private final FileUtil fileUtil;
    private final RequestHandler staticResourceHandler;
    public RequestHandlerMapperImpl(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
        this.staticResourceHandler = new StaticResourceHandler(fileUtil);
        mappers = new HashMap<>();
    }

    @Override
    public void setRequestHandler(String path,HttpMethod method,RequestHandler requestHandler) {
        mappers.computeIfAbsent(path, k->new HashMap<>())
                .put(method,requestHandler);
    }

    public RequestHandler getRequestHandler(String path,HttpMethod method) {
        if (isStaticFileRequest(path)) {
            return staticResourceHandler;
        }
        Map<HttpMethod,RequestHandler> requestHandlers = mappers.get(path);
        if(requestHandlers == null){
            throw new HttpNotFoundException(path+" not found");
        }
        RequestHandler requestHandler = requestHandlers.get(method);
        if(requestHandler == null){
            throw new HttpMethodNotAllowedException(path+" "+method+" not found");
        }
        return requestHandler;
    }

    private boolean isStaticFileRequest(String path) {
        return fileUtil.isFilePath(path);
    }
}
