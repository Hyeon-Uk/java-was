package codesquad.application.handler;

import codesquad.application.model.User;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.engine.HttpTemplateEngine;
import codesquad.was.http.exception.HttpNotFoundException;
import codesquad.was.http.handler.RequestHandler;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.message.response.HttpStatus;
import codesquad.was.http.session.Session;
import codesquad.was.utils.FileUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Coffee(name="userList")
@RequestMapping(path = "/user/list")
public class UserListHandler implements RequestHandler {
    private final UserDatabase userDatabase;
    private final FileUtil fileUtil;
    public UserListHandler(FileUtil fileUtil,UserDatabase userDatabase) {
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
                rendered = HttpTemplateEngine.render(template, context);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            res.setBody(rendered);
        }catch(Exception e){
            throw new HttpNotFoundException(req.getUri().concat(" : request can not found"));
        }
    }
}
