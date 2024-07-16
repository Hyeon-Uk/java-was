package codesquad.application.handler;

import codesquad.application.model.User;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.Controller;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.framework.dispatcher.mv.Model;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.session.Session;

import java.util.Optional;
import java.util.UUID;

@Controller
@Coffee
public class BoardController {
    @RequestMapping(path = "/write",method= HttpMethod.GET)
    public String writePage(Session session, Model model){
        if(session == null){
            return "redirect:/login";
        }

        Optional<Object> user1 = session.get("user");
        if(user1.isEmpty()) return "redirect:/login";

        User user = (User)user1.get();
        model.addAttribute("user",user);
        model.addAttribute("name",user.getNickname());

        String csrfToken = UUID.randomUUID().toString();
        model.addAttribute("csrfToken",csrfToken);
        session.set("csrfToken",csrfToken);
        return "article/index";
    }
}
