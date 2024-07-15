package codesquad.application.handler;

import codesquad.application.model.User;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.Controller;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.cookie.Cookie;
import codesquad.was.http.engine.HttpTemplateEngine;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.http.session.Session;

import java.util.Optional;

@Controller
@Coffee
public class UserAuthController {
    private final UserDatabase userDatabase;
    private final HttpTemplateEngine templateEngine;

    public UserAuthController(UserDatabase userDatabase, HttpTemplateEngine templateEngine) {
        this.userDatabase = userDatabase;
        this.templateEngine = templateEngine;
    }

    @RequestMapping(path="/user/create",method = HttpMethod.POST)
    public void registerUser(HttpRequest req, HttpResponse res){
        String id = req.getQueryString("userId");
        String nickname = req.getQueryString("nickname");
        String password = req.getQueryString("password");

        User user = new User(id,password,nickname);

        userDatabase.save(user);

        res.sendRedirect("/");
    }

    @RequestMapping(path="/login",method = HttpMethod.POST)
    public void loginProcess(HttpRequest req, HttpResponse res){
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
        res.sendRedirect("/");
    }

    @RequestMapping(path = "/logout",method=HttpMethod.POST)
    public void logout(HttpRequest req, HttpResponse res){
        Session session = req.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        Cookie sid = new Cookie("SID", "");
        sid.setMaxAge(0);
        res.addCookie(sid);
        res.sendRedirect("/");
    }
}
