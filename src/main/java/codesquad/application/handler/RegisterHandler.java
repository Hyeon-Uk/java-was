package codesquad.application.handler;

import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.application.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterHandler implements RequestHandler {
    private final Logger logger = LoggerFactory.getLogger(RegisterHandler.class);
    @Override
    public void getHandle(HttpRequest req, HttpResponse res) {
        String id = req.getQueryString("userId");
        String nickname = req.getQueryString("nickname");
        String password = req.getQueryString("password");

        User user = new User(id,password,nickname);
        logger.info("User = {}",user);

        res.sendRedirect("/");
    }
}
