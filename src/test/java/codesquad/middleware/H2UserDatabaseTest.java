package codesquad.middleware;

import codesquad.application.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class H2UserDatabaseTest {
    private DataSource dataSource = new MyH2DataSource();
    private UserDatabase userDatabase = new H2UserDatabase(dataSource);

    @BeforeEach
    void setUp(){
        try{
            Class.forName(dataSource.getDriverClassName());

            Connection con = DriverManager.getConnection(dataSource.getUrl(),dataSource.getUsername(),dataSource.getPassword());
            con.prepareStatement("delete from \"USER\"");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
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
        assertEquals(user, optional.get());
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
        assertEquals(overrideUser,optional.get());
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

        //when
        List<User> allUsers = userDatabase.findAll();

        //then
        assertTrue(allUsers.containsAll(List.of(user1,user2,user3)));
    }
}