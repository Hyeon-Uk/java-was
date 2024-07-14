package codesquad.application.handler;

import codesquad.application.model.User;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.cookie.Cookie;
import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.http.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


@Coffee(name="login")
public class LoginHandler implements RequestHandler {
    private final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    private final UserDatabase userDatabase;
    public LoginHandler(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    @Override
    public void getHandle(HttpRequest req, HttpResponse res) {
        RequestHandler.super.getHandle(req,res);
    }

    @Override
    public void postHandle(HttpRequest req, HttpResponse res) {
        String userId = req.getQueryString("userId");
        String password = req.getQueryString("password");

        Optional<User> byId = userDatabase.findById(userId);
        if(!byId.isPresent()) {
            res.sendRedirect("/user/login_failed.html");
            return;
        }

        User user = byId.get();
        if(!password.equals(user.getPassword())) {
            res.sendRedirect("/user/login_failed.html");
            return;
        }
        Session session = req.getSession(true);
        session.set("user",user);
        res.addCookie(new Cookie("SID",session.getId()));
        res.sendRedirect("/main");
    }
}
