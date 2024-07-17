package codesquad.application.handler;

import codesquad.application.dto.UserLogin;
import codesquad.application.dto.UserRegist;
import codesquad.application.handler.mock.MockPasswordEncoder;
import codesquad.application.handler.mock.MockUserDatabase;
import codesquad.application.model.User;
import codesquad.application.utils.PasswordEncoder;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.exception.HttpBadRequestException;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.http.session.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserAuthControllerTest {
    private PasswordEncoder passwordEncoder = new MockPasswordEncoder();
    private UserDatabase userDatabase = new MockUserDatabase();
    private UserAuthController userAuthController = new UserAuthController(userDatabase, passwordEncoder);

    @Nested
    @DisplayName("Register")
    class RegisterTest{
        /**
         * 1. 정상적인 회원가입 로직
         * 2. 이미 존재하는 id로 가입하려했을때 에러
         */
        @Test
        void registerUserSuccess(){
            //given
            String userId = "userId";
            String password = "password";
            String nickname = "nickname";
            UserRegist req = new UserRegist(userId,password,nickname);

            //when
            String path = userAuthController.registerUser(req);

            //then
            assertEquals("redirect:/",path);
            Optional<User> optional = userDatabase.findById(userId);
            assertTrue(optional.isPresent());
            User user = optional.get();
            assertEquals(userId,user.getId());
            assertEquals(nickname,user.getNickname());
            //must be encrypted
            assertNotEquals(password,user.getPassword());
            assertTrue(passwordEncoder.match(password,user.getPassword()));
        }
        @Test
        void registerFailedBecauseOfDuplicatedUser(){
            //given
            Map<String,String> queryString = new HashMap<>();
            String id = "id";
            String password = "password";
            String nickname = "nickname";
            userDatabase.save(new User(id,password,nickname));
            UserRegist req = new UserRegist(id,password,"other nickname");

            //when & then
            assertThrows(HttpBadRequestException.class,()->{
                userAuthController.registerUser(req);
            });
        }
    }

    @Nested
    @DisplayName("login")
    class LoginTest{
        /**
         * 1. 유저의 로그인이 성공하는 케이스
         * 2. 존재하지않는 아이디로 로그인했을경우
         * 3. 아이디는 존재하지만 password가 일치하지 않는 경우
         */
        private String id = "id";
        private String password = "password";
        private String nickname = "nickname";
        @BeforeEach
        void userSetUp(){
            userDatabase.save(new User(id,passwordEncoder.encode(password),nickname));
        }

        //TODO : 로그인이 끝난 뒤 session에 객체가 남아있고, 쿠키값이 날아가는것을 검증해야함
        @Test
        void loginSuccess(){
            //given
            UserLogin req = new UserLogin(id,password);
            Session session = new Session(new Date(),new Date());
            HttpResponse res = MockFactory.getHttpResponse();
            User expected = new User(id,passwordEncoder.encode(password),nickname);

            //when
            String path = userAuthController.loginProcess(req, session, res);

            //then
            assertEquals("redirect:/",path);
            Optional<Object> optional = session.get("user");
            assertTrue(optional.isPresent());
            assertEquals(expected,optional.get());
        }

        @Test
        void loginFailedWithNonExistsUser(){
            //given
            UserLogin req = new UserLogin("nonExistsUser",password);
            Session session = new Session(new Date(),new Date());
            HttpResponse res = MockFactory.getHttpResponse();
            //when
            String path = userAuthController.loginProcess(req,session, res);

            //then
            assertEquals("redirect:/user/login_failed",path);
        }

        @Test
        void loginFailedWithWrongPassword(){
            //given
            UserLogin req = new UserLogin("id","wrongPassword");
            Session session = new Session(new Date(),new Date());
            HttpResponse res = MockFactory.getHttpResponse();
            //when
            String path = userAuthController.loginProcess(req,session, res);

            //then
            assertEquals("redirect:/user/login_failed",path);
        }
    }

    @Nested
    @DisplayName("logout")
    class LogoutTest{
        //TODO : 만료시키는 쿠키를 날리는지 검증해야함
        @Test
        public void logoutSuccess() throws Exception {
            //given
            Session session = new Session(new Date(),new Date());
            HttpResponse res = MockFactory.getHttpResponse();

            //when
            String path = userAuthController.logout(session, res);

            //then
            assertEquals("redirect:/",path);
            assertTrue(session.isExpired());
        }

        @Test
        public void logoutWithNullSession() throws Exception {
            //given
            HttpResponse res = MockFactory.getHttpResponse();
            //when
            String path = userAuthController.logout(null, res);
            //then
            assertEquals("redirect:/",path);

        }
    }
}
