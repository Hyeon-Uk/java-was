package codesquad.application.handler;

import codesquad.application.model.Board;
import codesquad.application.model.User;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.Controller;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.framework.dispatcher.mv.Model;
import codesquad.framework.resolver.annotation.SessionParam;
import codesquad.middleware.BoardDatabase;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.session.Session;

import java.util.List;

@Controller
@Coffee
public class MainController {
    private final UserDatabase userDatabase;
    private final BoardDatabase boardDatabase;

    public MainController(UserDatabase userDatabase,BoardDatabase boardDatabase) {
        this.userDatabase = userDatabase;
        this.boardDatabase = boardDatabase;
    }

    @RequestMapping(path="/",method = HttpMethod.GET)
    public String mainPage(@SessionParam(create=false) Session session, Model model){
        //user의 session부분
        if(session != null){
            session.get("user")
                .ifPresent((user)->{
                    User usr = (User)user;
                    model.addAttribute("user", usr);
                    model.addAttribute("name", usr.getNickname());
                });
        }

        //board 출력 부분
        List<Board> boards = boardDatabase.findAll();
        model.addAttribute("boards",boards);

        return "index";
    }

    @RequestMapping(path ="/user/login_failed")
    public String loginFailed(){
        return "user/login_failed";
    }
}
