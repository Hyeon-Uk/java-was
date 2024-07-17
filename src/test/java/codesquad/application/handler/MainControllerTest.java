package codesquad.application.handler;

import codesquad.application.handler.mock.MockBoardDatabase;
import codesquad.application.handler.mock.MockUserDatabase;
import codesquad.application.model.Board;
import codesquad.application.model.User;
import codesquad.framework.dispatcher.mv.Model;
import codesquad.message.mock.MockTimer;
import codesquad.middleware.BoardDatabase;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.session.Session;
import codesquad.was.utils.Timer;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MainControllerTest {
    private BoardDatabase boardDatabase = new MockBoardDatabase();
    private UserDatabase userDatabase = new MockUserDatabase();
    private MainController controller = new MainController(userDatabase,boardDatabase);
    private long currentTime = 1000l;
    private Timer timer = new MockTimer(currentTime);
    @Test
    void mainPageWithLogin(){
        //given
        Session session = new Session(timer.getCurrentTime(),timer.getCurrentTime());
        User user = new User("id", "password", "nickname");
        session.set("user",user);
        Model model = new Model();

        //when
        String path = controller.mainPage(session, model);

        //then
        assertEquals("index",path);
        assertEquals(user,model.getAttribute("user"));
        assertEquals(user.getNickname(),model.getAttribute("name"));
    }

    @Test
    void mainPageWithManyBoards(){
        //given
        List<Board> boardList = List.of(new Board(1l,"title1","content1","writer1"),
                new Board(2l,"title2","content2","writer2"),
                new Board(3l,"title3","content3","writer3"));
        boardList.forEach(boardDatabase::save);
        Model model = new Model();
        Session session = new Session(timer.getCurrentTime(),timer.getCurrentTime());

        //when
        String path = controller.mainPage(session, model);

        //then
        assertEquals("index",path);
        List<Board> boards = (List<Board>) model.getAttribute("boards");
        assertNotNull(boards);
        assertTrue(boards.containsAll(boardList));
    }

    @Test
    void mainPageWithoutLogin1(){
        //given
        Model model = new Model();

        //when
        String path = controller.mainPage(null, model);

        //then
        assertEquals("index",path);
        assertNull(model.getAttribute("user"));
        assertNull(model.getAttribute("name"));
    }

    @Test
    void mainPageWithEmptySession(){
        //given
        Model model = new Model();
        Session session = new Session(timer.getCurrentTime(),timer.getCurrentTime());

        //when
        String path = controller.mainPage(session, model);

        //then
        assertEquals("index",path);
        assertNull(model.getAttribute("user"));
        assertNull(model.getAttribute("name"));
    }

    @Test
    void loginFailedPage(){
        //given

        //when
        String path = controller.loginFailed();

        //then
        assertEquals("user/login_failed",path);
    }
}