package codesquad.was.http.handler;

import codesquad.was.http.message.request.Request;
import codesquad.was.http.message.response.Response;


public interface RequestHandler {
    void handle(Request req, Response res);
}
