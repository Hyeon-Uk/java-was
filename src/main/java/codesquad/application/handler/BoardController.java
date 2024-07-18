package codesquad.application.handler;

import codesquad.application.dto.BoardRegist;
import codesquad.application.model.Board;
import codesquad.application.model.User;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.Controller;
import codesquad.framework.coffee.annotation.RequestMapping;
import codesquad.framework.dispatcher.mv.Model;
import codesquad.framework.resolver.annotation.RequestParam;
import codesquad.framework.resolver.annotation.SessionParam;
import codesquad.middleware.BoardDatabase;
import codesquad.middleware.FileDatabase;
import codesquad.middleware.FileSystemDatabase;
import codesquad.was.http.exception.HttpBadRequestException;
import codesquad.was.http.exception.HttpNotFoundException;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.vo.HttpFile;
import codesquad.was.http.session.Session;

import java.util.Optional;
import java.util.UUID;

@Controller
@Coffee
public class BoardController {
    private final BoardDatabase boardDatabase;
    private final FileDatabase fileDatabase;
    public BoardController(BoardDatabase boardDatabase, FileDatabase fileDatabase) {
        this.boardDatabase = boardDatabase;
        this.fileDatabase = fileDatabase;
    }

    @RequestMapping(path="/board",method=HttpMethod.GET)
    public String boardPage(@RequestParam(name="id") Long boardId,
                            @SessionParam(create = false) Session session,
                            Model model){
        Optional<Board> byId = boardDatabase.findById(boardId);

        if(byId.isEmpty()) throw new HttpNotFoundException(boardId+" 게시글을 찾을 수 없습니다.");
        Board board = byId.get();

        if(session != null) {
            session.get("user")
                    .map(User.class::cast)
                    .ifPresent(user -> {
                        model.addAttribute("user", user);
                        model.addAttribute("name", user.getNickname());
                    });
        }

        model.addAttribute("writer",board.getWriter());
        model.addAttribute("title",board.getTitle());
        model.addAttribute("content",board.getContent());
        model.addAttribute("path",board.getImagePath());

        return "/article/content";
    }

    @RequestMapping(path = "/write",method= HttpMethod.GET)
    public String writePage(@SessionParam(create=false) Session session, Model model){
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

    @RequestMapping(path ="/write",method=HttpMethod.POST)
    public String registBoard(@SessionParam(create = false) Session session,
                              @RequestParam BoardRegist req){
        if(session == null){
            return "redirect:/login";
        }
        String sessionCsrf = session.get("csrfToken")
                                .map(String.class::cast)
                                .orElse(null);

        if(sessionCsrf == null) return "redirect:/";

        Optional<User> sessionUser = session.get("user").map(User.class::cast);
        if(sessionUser.isEmpty()) return "redirect:/login";
        User user = sessionUser.get();
        String title = req.getTitle();
        String content = req.getContent();
        String csrfToken = req.getCsrfToken();
        String writer = user.getId();
        HttpFile file = req.getFile();

        if(file != null && (!file.isExist() || !file.isImageFile())){
            throw new HttpBadRequestException("이미지 파일형식을 넣어주세요");
        }

        if(csrfToken != null && csrfToken.equals(sessionCsrf)){
            String savedPath = null;
            Board board = null;
            if(file!=null&&file.isExist()){
                savedPath = fileDatabase.save(file);
                board = new Board(title,content,writer,"/"+savedPath);
            }
            else{
                board = new Board(title,content,writer);
            }
            boardDatabase.save(board);
            return "redirect:/";
        }
        return "redirect:/";
    }
}
