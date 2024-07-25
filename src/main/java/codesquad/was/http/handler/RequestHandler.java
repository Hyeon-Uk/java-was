package codesquad.was.http.handler;

import codesquad.was.http.exception.HttpMethodNotAllowedException;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.utils.FileUtils;


public interface RequestHandler {
    void handle(HttpRequest req, HttpResponse res);
}
