package codesquad.application.handler;

import codesquad.application.model.User;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.Controller;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.framework.coffee.mvc.Model;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
@Coffee
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserDatabase userDatabase;

    public UserController(UserDatabase userDatabase){
        this.userDatabase = userDatabase;
    }

    @RequestMapping(path="/registration",method = HttpMethod.GET)
    public String registration(){
        return "registration/index";
    }

    @RequestMapping(path="/login",method=HttpMethod.GET)
    public String loginPage(HttpRequest req, HttpResponse res){
        return "login/index";
    }

    @RequestMapping(path="/user/list",method = HttpMethod.GET)
    public String getUserList(Session session, Model model){
        if(session == null || session.get("user").isEmpty()){
            return "redirect:/login";
        }

        List<User> users = userDatabase.findAll();

        User user = (User)session.get("user").get();
        model.addAttribute("user",user);
        model.addAttribute("name",user.getNickname());
        model.addAttribute("users",users);

        return "user/list/index";
    }
}
