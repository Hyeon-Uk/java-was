package codesquad.application.handler;

import codesquad.application.handler.mock.MockUserDatabase;
import codesquad.application.model.User;
import codesquad.framework.coffee.mvc.Model;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.session.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private UserDatabase userDatabase = new MockUserDatabase();
    private UserController userController = new UserController(userDatabase);

    @Nested
    @DisplayName("registration")
    class Registration {
        @Test
        void registrationPageTest(){
            //given

            //when
            String path = userController.registration();

            //then
            assertEquals("registration/index",path);
        }
    }

    @Nested
    @DisplayName("login")
    class Login{
        @Test
        void loginPageTest(){
            //given

            //when
            String path = userController.loginPage();

            //then
            assertEquals("login/index",path);
        }
    }

    @Nested
    @DisplayName("userList")
    class UserList{
        /**
         * 1. 로그인된 상태로 정상적인 요청
         * 2. 로그인이 되지 않은 케이스
         * 3. session이 존재하지만 session안에 user값이 없는 케이스
         */
        private Session session = new Session(new Date(),new Date());
        User user1 = new User("id1","password1","nickname1");
        User user2 = new User("id2","password2","nickname2");
        User user3 = new User("id3","password3","nickname3");
        Model model = new Model();
        @BeforeEach
        void usersSetUp(){
            userDatabase.save(user1);
            userDatabase.save(user2);
            userDatabase.save(user3);
        }

        @Test
        void userListPageSuccess(){
            //given
            session.set("user",user1);

            //when
            String path = userController.getUserList(session, model);

            //then
            Object modelUser = model.getAttribute("user");
            Object modelName = model.getAttribute("name");
            List<User> modelUsers = (List)model.getAttribute("users");
            assertEquals("user/list/index",path);
            assertEquals(modelUser,user1);
            assertEquals(modelName,user1.getNickname());
            assertTrue(modelUsers.containsAll(List.of(user1,user2,user3)));
        }

        @Test
        void userListPageFailBecauseOfNullSession(){
            //given

            //when
            String path = userController.getUserList(null, model);

            //then
            assertEquals("redirect:/login",path);
        }

        @Test
        void userListPageFailBecauseOfNullSessionUser(){
            //given

            //when
            String path = userController.getUserList(session, model);

            //then
            assertEquals("redirect:/login",path);
        }
    }
}