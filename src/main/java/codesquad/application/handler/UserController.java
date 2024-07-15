package codesquad.application.handler;

import codesquad.application.model.User;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.Controller;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.engine.HttpTemplateEngine;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.http.session.Session;
import codesquad.was.utils.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static codesquad.was.http.message.response.HttpStatus.OK;

@Controller
@Coffee
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final FileUtil fileUtil;
    private final UserDatabase userDatabase;
    private final HttpTemplateEngine httpTemplateEngine;

    public UserController(FileUtil fileUtil,
                          UserDatabase userDatabase,
                          HttpTemplateEngine httpTemplateEngine) {
        this.fileUtil = fileUtil;
        this.userDatabase = userDatabase;
        this.httpTemplateEngine = httpTemplateEngine;
    }

    @RequestMapping(path="/registration",method = HttpMethod.GET)
    public void registration(HttpRequest req, HttpResponse res){
        byte[] bytes = fileUtil.readStaticFile("/registration/index.html");
        res.setBody(bytes);
        res.setStatus(OK);
    }

    @RequestMapping(path="/login",method=HttpMethod.GET)
    public void loginPage(HttpRequest req, HttpResponse res){
        byte[] body = fileUtil.readStaticFile("/login/index.html");
        res.setBody(body);
        res.setStatus(OK);
    }

    @RequestMapping(path="/user/list",method = HttpMethod.GET)
    public void getUserList(HttpRequest req, HttpResponse res){
        byte[] body = fileUtil.readStaticFile("/user/list/index.html");
        res.setBody(body);
        res.setStatus(HttpStatus.OK);
        String template = new String(body);

        Session session = req.getSession(false);
        if(session == null){
            res.sendRedirect("/login");
            return;
        }

        List<User> users = userDatabase.findAll();

        Map<String,Object> context = new HashMap<>();
        User user = (User)session.get("user")
                .orElse(new User(null,null,null));
        context.put("user",user);
        context.put("name",user.getNickname());
        context.put("users",users);

        String rendered = null;
        try {
            rendered = httpTemplateEngine.render(template, context);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        res.setBody(rendered);
        res.setStatus(OK);
    }
}
