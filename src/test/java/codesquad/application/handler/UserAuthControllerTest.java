package codesquad.application.handler;

import codesquad.application.handler.mock.MockPasswordEncoder;
import codesquad.application.handler.mock.MockUserDatabase;
import codesquad.application.model.User;
import codesquad.application.utils.PasswordEncoder;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.exception.HttpBadRequestException;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.request.HttpRequest;
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
            Map<String,String> queryString = new HashMap<>();
            String id = "id";
            String password = "password";
            String nickname = "nickname";
            queryString.put("userId",id);
            queryString.put("password",password);
            queryString.put("nickname",nickname);
            HttpRequest request = MockFactory.getHttpRequest(HttpMethod.POST,queryString,new HashMap<>(),"");

            //when
            String path = userAuthController.registerUser(request);

            //then
            assertEquals("redirect:/",path);
            Optional<User> optional = userDatabase.findById(id);
            assertTrue(optional.isPresent());
            User user = optional.get();
            assertEquals(id,user.getId());
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
            queryString.put("userId",id);
            queryString.put("password",password);
            queryString.put("nickname",nickname);
            HttpRequest request = MockFactory.getHttpRequest(HttpMethod.POST,queryString,new HashMap<>(),"");

            //when & then
            assertThrows(HttpBadRequestException.class,()->{
                userAuthController.registerUser(request);
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
            Map<String,String> queryString = new HashMap<>();
            queryString.put("userId",id);
            queryString.put("password",password);
            HttpRequest req = MockFactory.getHttpRequest(HttpMethod.POST,queryString,new HashMap<>(),"");
            HttpResponse res = MockFactory.getHttpResponse();
            //when
            String path = userAuthController.loginProcess(req, res);

            //then
            assertEquals("redirect:/",path);
        }

        @Test
        void loginFailedWithNonExistsUser(){
            //given
            Map<String,String> queryString = new HashMap<>();
            queryString.put("userId","nonExistsUser");
            queryString.put("password",password);
            HttpRequest req = MockFactory.getHttpRequest(HttpMethod.POST,queryString,new HashMap<>(),"");
            HttpResponse res = MockFactory.getHttpResponse();
            //when
            String path = userAuthController.loginProcess(req, res);

            //then
            assertEquals("redirect:/user/login_failed",path);
        }

        @Test
        void loginFailedWithWrongPassword(){
            //given
            Map<String,String> queryString = new HashMap<>();
            queryString.put("userId",id);
            queryString.put("password","wrongPassword");
            HttpRequest req = MockFactory.getHttpRequest(HttpMethod.POST,queryString,new HashMap<>(),"");
            HttpResponse res = MockFactory.getHttpResponse();
            //when
            String path = userAuthController.loginProcess(req, res);

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
    }
}
