package codesquad.middleware;

import codesquad.application.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDatabaseInMemoryTest {
    private UserDatabase userDatabase = new UserDatabaseInMemory();

    void compareUser(User expected,User actual){
        assertAll("compare user",
                ()->assertEquals(expected.getId(),actual.getId()),
                ()->assertEquals(expected.getNickname(),actual.getNickname()),
                ()->assertEquals(expected.getPassword(),actual.getPassword())
        );
    }

    @Test
    void saveSuccess(){
        //given
        String id = "id";
        String password = "password";
        String nickname = "nickname";
        User user = new User(id,password,nickname);

        //when
        userDatabase.save(user);

        //then
        Optional<User> optional = userDatabase.findById(id);
        assertTrue(optional.isPresent());
        compareUser(user, optional.get());
    }

    @Test
    void overrideSave(){
        //given
        String id = "id";
        String password = "password";
        String nickname = "nickname";
        User user = new User(id,password,nickname);
        userDatabase.save(user);

        //when
        String newNickname = "newNickname";
        User overrideUser = new User(id,password,newNickname);
        userDatabase.save(overrideUser);

        //then
        Optional<User> optional = userDatabase.findById(id);
        assertTrue(optional.isPresent());
        compareUser(overrideUser,optional.get());
    }

    @Test
    void findAll(){
        //given
        User user1 = new User("id1","password1","nickname1");
        User user2 = new User("id2","password2","nickname2");
        User user3 = new User("id3","password3","nickname3");
        userDatabase.save(user1);
        userDatabase.save(user2);
        userDatabase.save(user3);
        List<String> ids = List.of(user1.getId(),user2.getId(),user3.getId());

        //when
        List<User> allUsers = userDatabase.findAll();

        //then
        assertTrue(allUsers
                .stream()
                .map(User::getId)
                .allMatch(id->ids.contains(id))
        );
    }
}