package codesquad.application.handler;

import codesquad.application.handler.mock.MockUserDatabase;
import codesquad.middleware.UserDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
}