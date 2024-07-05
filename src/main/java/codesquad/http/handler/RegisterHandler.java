package codesquad.http.handler;

import codesquad.http.message.request.HttpRequestMessage;
import codesquad.http.message.response.HttpResponseMessage;
import codesquad.http.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterHandler implements RequestHandler{
    private final Logger logger = LoggerFactory.getLogger(RegisterHandler.class);
    @Override
    public void getHandle(HttpRequestMessage req, HttpResponseMessage res) {
        String id = req.getQueryString("userId");
        String nickname = req.getQueryString("nickname");
        String password = req.getQueryString("password");

        User user = new User(id,password,nickname);
        logger.info("User = {}",user);

        res.sendRedirect("/");
    }
}
