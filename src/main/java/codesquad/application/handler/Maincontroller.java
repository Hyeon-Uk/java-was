package codesquad.application.handler;

import codesquad.application.model.User;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.Controller;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.framework.coffee.mvc.Model;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.session.Session;

@Controller
@Coffee
public class Maincontroller {
    private final UserDatabase userDatabase;

    public Maincontroller(UserDatabase userDatabase) {
        this.userDatabase = userDatabase;
    }

    @RequestMapping(path="/",method = HttpMethod.GET)
    public String mainPage(Session session, Model model){
        if(session != null){
            session.get("user")
                .ifPresent((user)->{
                    User usr = (User)user;
                    model.addAttribute("user", usr);
                    model.addAttribute("name", usr.getNickname());
                });
        }
        return "index";
    }
}
