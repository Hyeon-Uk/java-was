package codesquad.application.handler;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.application.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Coffee(name="register")
@RequestMapping(path ="/user/create")
public class RegisterHandler implements RequestHandler {
    private final Logger logger = LoggerFactory.getLogger(RegisterHandler.class);
    private final UserDatabase userDatabase;
    public RegisterHandler(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }
    @Override
    public void postHandle(HttpRequest req, HttpResponse res) {
        String id = req.getQueryString("userId");
        String nickname = req.getQueryString("nickname");
        String password = req.getQueryString("password");

        User user = new User(id,password,nickname);

        userDatabase.save(user);
        logger.info("User = {}",user);

        res.sendRedirect("/");
    }
}
