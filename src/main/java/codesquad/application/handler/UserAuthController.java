package codesquad.application.handler;

import codesquad.application.model.User;
import codesquad.application.utils.PasswordEncoder;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.Controller;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.cookie.Cookie;
import codesquad.was.http.exception.HttpBadRequestException;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.session.Session;

import java.util.Optional;

@Controller
@Coffee
public class UserAuthController {
    private final UserDatabase userDatabase;
    private final PasswordEncoder passwordEncoder;

    public UserAuthController(UserDatabase userDatabase,PasswordEncoder passwordEncoder) {
        this.userDatabase = userDatabase;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(path="/user/create",method = HttpMethod.POST)
    public String registerUser(HttpRequest req){
        String id = req.getQueryString("userId");
        String nickname = req.getQueryString("nickname");
        String password = req.getQueryString("password");

        userDatabase.findById(id)
                .ifPresentOrElse((user)-> {
                    throw new HttpBadRequestException("Already exists user");
                },()->{
                    String encrypted = passwordEncoder.encode(password);
                    System.out.println("encrypted = " + encrypted);
                    userDatabase.save(new User(id,encrypted,nickname));
                });

        return "redirect:/";
    }

    @RequestMapping(path="/login",method = HttpMethod.POST)
    public String loginProcess(HttpRequest req, HttpResponse res){
        String userId = req.getQueryString("userId");
        String password = req.getQueryString("password");

        Optional<User> byId = userDatabase.findById(userId);
        if(!byId.isPresent()) {
            return "redirect:/user/login_failed";
        }

        User user = byId.get();
        if(!passwordEncoder.match(password,user.getPassword())) {
            return "redirect:/user/login_failed";
        }

        Session session = req.getSession(true);
        session.set("user",user);
        res.addCookie(new Cookie("SID",session.getId()));
        return "redirect:/";
    }

    @RequestMapping(path = "/logout",method=HttpMethod.POST)
    public String logout(Session session,HttpResponse res){
        if(session != null) {
            session.invalidate();
        }
        Cookie sid = new Cookie("SID", "");
        sid.setMaxAge(0);
        res.addCookie(sid);
        return "redirect:/";
    }
}
