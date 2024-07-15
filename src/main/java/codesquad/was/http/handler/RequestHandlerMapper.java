package codesquad.was.http.handler;

import codesquad.was.http.message.request.HttpMethod;

public interface RequestHandlerMapper {
    RequestHandler getRequestHandler(String path,HttpMethod method);
    void setRequestHandler(String path, HttpMethod method,RequestHandler requestHandler);
}
