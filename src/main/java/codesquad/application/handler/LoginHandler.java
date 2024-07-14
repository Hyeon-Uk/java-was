package codesquad.application.handler;

import codesquad.application.model.User;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.cookie.Cookie;
import codesquad.was.http.exception.HttpNotFoundException;
import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.http.session.Session;
import codesquad.was.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


@Coffee(name="login")
@RequestMapping(path = "/login")
public class LoginHandler implements RequestHandler {
    private final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    private final UserDatabase userDatabase;
    private final FileUtil fileUtil;
    public LoginHandler(FileUtil fileUtil,UserDatabase userDatabase) {
        this.fileUtil = fileUtil;
        this.userDatabase = userDatabase;
    }

    @Override
    public void getHandle(HttpRequest req, HttpResponse res) {
        String uri = req.getUri();
        if(uri.lastIndexOf("/") == uri.length()-1){
            uri = uri.concat("index.html");
        }
        else{
            uri = uri.concat("/index.html");
        }
        try {
            byte[] body = fileUtil.readStaticFile(uri);
            res.setBody(body);
            res.setStatus(HttpStatus.OK);
        }catch(Exception e){
            throw new HttpNotFoundException(req.getUri().concat(" : request can not found"));
        }
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
        res.sendRedirect("/");
    }
}
