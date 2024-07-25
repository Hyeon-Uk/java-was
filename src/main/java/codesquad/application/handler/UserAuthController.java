package codesquad.application.handler;

import codesquad.application.dto.UserLogin;
import codesquad.application.dto.UserRegist;
import codesquad.application.model.User;
import codesquad.application.utils.PasswordEncoder;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.Controller;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.framework.resolver.annotation.RequestParam;
import codesquad.framework.resolver.annotation.SessionParam;
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
    public String registerUser(@RequestParam UserRegist req){
        String id = req.getUserId();
        String nickname = req.getNickname();
        String password = req.getPassword();

        userDatabase.findById(id)
                .ifPresentOrElse((user)-> {
                    throw new HttpBadRequestException("Already exists user");
                },()->{
                    String encrypted = passwordEncoder.encode(password);
                    userDatabase.save(new User(id,encrypted,nickname));
                });

        return "redirect:/";
    }

    @RequestMapping(path="/login",method = HttpMethod.POST)
    public String loginProcess(@RequestParam UserLogin req,
                               @SessionParam(create=true) Session session,
                               HttpResponse res){
        String userId = req.getUserId();
        String password = req.getPassword();

        Optional<User> byId = userDatabase.findById(userId);
        if(!byId.isPresent()) {
            return "redirect:/user/login_failed";
        }

        User user = byId.get();
        if(!passwordEncoder.match(password,user.getPassword())) {
            return "redirect:/user/login_failed";
        }

        session.set("user",user);
        res.addCookie(new Cookie("SID",session.getId()));
        return "redirect:/";
    }

    @RequestMapping(path = "/logout",method=HttpMethod.POST)
    public String logout(@SessionParam(create=false) Session session,HttpResponse res){
        if(session != null) {
            session.invalidate();
        }
        Cookie sid = new Cookie("SID", "");
        sid.setMaxAge(0);
        res.addCookie(sid);
        return "redirect:/";
    }
}
