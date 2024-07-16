package codesquad.application.handler;

import codesquad.application.handler.mock.MockPasswordEncoder;
import codesquad.application.handler.mock.MockUserDatabase;
import codesquad.application.model.User;
import codesquad.application.utils.PasswordEncoder;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.exception.HttpBadRequestException;
import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
}
