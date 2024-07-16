package codesquad.application.handler;

import codesquad.application.model.User;
import codesquad.framework.dispatcher.mv.Model;
import codesquad.was.http.session.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class BoardControllerTest {
    BoardController boardController = new BoardController();

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
}