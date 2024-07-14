package codesquad.was.http.handler;

public interface RequestHandlerMapper {
    RequestHandler getRequestHandler(String path);
    void setRequestHandler(String path,RequestHandler requestHandler);
}
