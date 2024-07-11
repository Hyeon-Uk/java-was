package codesquad.application.handler;

import codesquad.application.model.User;
import codesquad.was.http.engine.HttpTemplateEngine;
import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.session.Session;

import java.util.HashMap;
import java.util.Map;

public class MainHandler implements RequestHandler {
    @Override
    public void getHandle(HttpRequest req, HttpResponse res) {
        RequestHandler.super.getHandle(req, res);

        byte[] body = res.getBody();
        String bodyString = new String(body);

        Session session = req.getSession(false);
        Map<String, Object> context = new HashMap<>();
        if(session!=null) {
            User user = (User)session.get("user")
                    .orElse(null);
            context.put("user", user);
            context.put("name",user == null ? null : user.getId());
        }
        String rendered = null;
        try {
            rendered = HttpTemplateEngine.render(bodyString, context);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        res.setBody(rendered);
    }
}
