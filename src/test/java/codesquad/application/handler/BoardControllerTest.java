package codesquad.application.handler;

import codesquad.application.dto.BoardRegist;
import codesquad.application.handler.mock.MockBoardDatabase;
import codesquad.application.model.Board;
import codesquad.application.model.User;
import codesquad.framework.dispatcher.mv.Model;
import codesquad.middleware.BoardDatabase;
import codesquad.was.http.session.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BoardControllerTest {
    private BoardDatabase boardDatabase = new MockBoardDatabase();
    private BoardController boardController = new BoardController(boardDatabase);

    @Nested
    @DisplayName("write page")
    class WritePage {
        Session session = new Session(new Date(),new Date());
        Model model = new Model();
        User user = new User("id1","password1","nickname1");

        @Test
        void success(){
            //given
            session.set("user",user);

            //when
            String path = boardController.writePage(session, model);

            //then
            assertEquals("article/index",path);
            assertEquals(user,model.getAttribute("user"));
            assertEquals(user.getNickname(),model.getAttribute("name"));
            assertNotNull(model.getAttribute("csrfToken"));
        }

        @Test
        void sessionIsNull(){
            //given

            //when
            String path = boardController.writePage(null, model);

            //then
            assertEquals("redirect:/login",path);
        }

        @Test
        void sessionUserIsNull(){
            //given

            //when
            String path = boardController.writePage(session, model);

            //then
            assertEquals("redirect:/login",path);
        }
    }

    @Nested
    @DisplayName("regist board")
    class RegistBoard {
        /**
         * 1. 정상적으로 수행후 redirect /
         * 2. csrf토큰이 일치하지 않으면 redirect /
         * 3. csrf토큰이 존재하지 않으면 redirect /
         * 4. session이 존재하지 않으면 redirect login
         * 5. session에 user가 존재하지 않으면 redirect login
         * 6. board의 csrf토큰이 null일 경우 redirect /
         */

        @Test
        void success(){
            //given
            Session session = new Session(new Date(),new Date());
            User user = new User("id","password","nickname");
            String csrfToken = UUID.randomUUID().toString();
            session.set("user",user);
            session.set("csrfToken",csrfToken);

            BoardRegist board = new BoardRegist("title","content",csrfToken);

            //when
            String path = boardController.registBoard(session, board);

            //then
            assertEquals("redirect:/",path);
            long size = boardDatabase.findAll()
                    .stream().filter(b -> b.getWriter().equals("id"))
                    .count();
            assertEquals(1,size);
        }

        @Test
        void notMathCsrfToken(){
            //given
            Session session = new Session(new Date(),new Date());
            User user = new User("id","password","nickname");
            String csrfToekn = UUID.randomUUID().toString();
            String otherCsrfToken = UUID.randomUUID().toString();
            session.set("user",user);
            session.set("csrfToken",otherCsrfToken);

            BoardRegist board = new BoardRegist("title","content",csrfToekn);

            //when
            String path = boardController.registBoard(session, board);

            //then
            assertEquals("redirect:/",path);
            long size = boardDatabase.findAll()
                    .stream().filter(b -> b.getWriter().equals("id"))
                    .count();
            assertEquals(0,size);
        }

        @Test
        void nullCsrfToken(){
            //given
            Session session = new Session(new Date(),new Date());
            User user = new User("id","password","nickname");
            String csrfToekn = UUID.randomUUID().toString();
            session.set("user",user);

            BoardRegist board = new BoardRegist("title","content",csrfToekn);

            //when
            String path = boardController.registBoard(session, board);

            //then
            assertEquals("redirect:/",path);
            long size = boardDatabase.findAll()
                    .stream().filter(b -> b.getWriter().equals("id"))
                    .count();
            assertEquals(0,size);
        }

        @Test
        void nullSession(){
            //given
            User user = new User("id","password","nickname");
            String csrfToken = UUID.randomUUID().toString();

            BoardRegist board = new BoardRegist("title","content",csrfToken);

            //when
            String path = boardController.registBoard(null, board);

            //then
            assertEquals("redirect:/login",path);
            long size = boardDatabase.findAll()
                    .stream().filter(b -> b.getWriter().equals("id"))
                    .count();
            assertEquals(0,size);
        }

        @Test
        void nullUserInSession(){
            //given
            Session session = new Session(new Date(),new Date());
            String csrfToken = UUID.randomUUID().toString();
            session.set("csrfToken",csrfToken);

            BoardRegist board = new BoardRegist("title","content",csrfToken);

            //when
            String path = boardController.registBoard(session, board);

            //then
            assertEquals("redirect:/login",path);
            long size = boardDatabase.findAll()
                    .stream().filter(b -> b.getWriter().equals("id"))
                    .count();
            assertEquals(0,size);
        }

        @Test
        void nullCsrfTokenInBoard(){
            //given
            Session session = new Session(new Date(),new Date());
            User user = new User("id","password","nickname");
            String csrfToken = UUID.randomUUID().toString();
            session.set("user",user);
            session.set("csrfToken",csrfToken);

            BoardRegist board = new BoardRegist("title","content",null);

            //when
            String path = boardController.registBoard(session, board);

            //then
            assertEquals("redirect:/",path);
            long size = boardDatabase.findAll()
                    .stream().filter(b -> b.getWriter().equals("id"))
                    .count();
            assertEquals(0,size);
        }
    }
}