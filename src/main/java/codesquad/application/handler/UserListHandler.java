package codesquad.application.handler;

import codesquad.application.model.User;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.engine.HttpTemplateEngine;
import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.session.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UserListHandler implements RequestHandler {
    @Override
    public void getHandle(HttpRequest req, HttpResponse res) {
        RequestHandler.super.getHandle(req, res);

        byte[] body = res.getBody();
        String template = new String(body);

        Session session = req.getSession(false);
        if(session == null){
            res.sendRedirect("/login");
            return;
        }

        List<User> users = UserDatabase.findAll();

        Map<String,Object> context = new HashMap<>();
        User user = (User)session.get("user")
                .orElse(new User(null,null,null));
        context.put("user",user);
        context.put("name",user.getNickname());
        context.put("users",users);

        String rendered = null;
        try {
            rendered = HttpTemplateEngine.render(template, context);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        res.setBody(rendered);
    }
}
