package codesquad.framework.dispatcher.servlet;

import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.message.request.HttpMethod;

public interface RequestHandlerMapper {
    RequestHandler getRequestHandler(String path, HttpMethod method);
    void setRequestHandler(String path, HttpMethod method,RequestHandler requestHandler);
}
