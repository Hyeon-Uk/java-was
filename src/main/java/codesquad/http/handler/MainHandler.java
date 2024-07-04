package codesquad.http.handler;

import codesquad.http.message.request.HttpRequestMessage;
import codesquad.http.message.response.HttpResponseMessage;

public class MainHandler implements RequestHandler{
    @Override
    public void getHandle(HttpRequestMessage req, HttpResponseMessage res) {
        res.sendRedirect("/index.html");
    }
}
