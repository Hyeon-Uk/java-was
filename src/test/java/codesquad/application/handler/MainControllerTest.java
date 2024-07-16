package codesquad.application.handler;

import codesquad.application.handler.mock.MockUserDatabase;
import codesquad.application.model.User;
import codesquad.framework.dispatcher.mv.Model;
import codesquad.message.mock.MockTimer;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.session.Session;
import codesquad.was.utils.Timer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MainControllerTest {
    private UserDatabase userDatabase = new MockUserDatabase();
    private MainController controller = new MainController(userDatabase);
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