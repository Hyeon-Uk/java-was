package codesquad.application.handler;

import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;

public class MainHandler implements RequestHandler {
    @Override
    public void getHandle(HttpRequest req, HttpResponse res) {
        res.sendRedirect("/index.html");
    }
}
