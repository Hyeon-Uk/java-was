package codesquad.application.handler;

import codesquad.application.model.User;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.was.http.engine.HttpTemplateEngine;
import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.http.session.Session;
import codesquad.was.utils.FileUtil;
import codesquad.was.utils.FileUtils;

import java.util.HashMap;
import java.util.Map;

@Coffee(name="main")
@RequestMapping(path="/")
public class MainHandler implements RequestHandler {
    private final FileUtil fileUtil;

    public MainHandler(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    @Override
    public void getHandle(HttpRequest req, HttpResponse res) {
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
            rendered = HttpTemplateEngine.render(bodyString, context);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        res.setBody(rendered);
        res.setStatus(HttpStatus.OK);
    }
}
