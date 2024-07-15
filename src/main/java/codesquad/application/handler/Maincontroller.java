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

import java.util.HashMap;
import java.util.Map;

@Controller
@Coffee
public class Maincontroller {
    private final UserDatabase userDatabase;
    private final FileUtil fileUtil;
    private final HttpTemplateEngine httpTemplateEngine;

    public Maincontroller(UserDatabase userDatabase, FileUtil fileUtil, HttpTemplateEngine httpTemplateEngine) {
        this.userDatabase = userDatabase;
        this.fileUtil = fileUtil;
        this.httpTemplateEngine = httpTemplateEngine;
    }

    @RequestMapping(path="/",method = HttpMethod.GET)
    public void mainPage(HttpRequest req,HttpResponse res){
        byte[] body = fileUtil.readStaticFile("/index.html");
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
            rendered = httpTemplateEngine.render(bodyString, context);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        res.setBody(rendered);
        res.setStatus(HttpStatus.OK);
    }
}
