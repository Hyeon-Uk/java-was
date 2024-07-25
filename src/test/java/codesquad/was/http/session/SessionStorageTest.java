package codesquad.was.http.session;

import codesquad.message.mock.MockTimer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * session storage
 * 1. 세션을 저장하면 시간을 셋팅해준다.
 */
class SessionStorageTest {
    private SessionStorage sessionStorage;
    private Session session;
    @BeforeEach
    void setUp(){
        sessionStorage = new SessionStorage();
        session = new Session(new Date(),new Date());
    }

    @Test
    void addSession(){
        //given
        String key = "sessionId";
        session.setId(key);

        //when
        sessionStorage.add(key,session);
        Optional<Session> s = sessionStorage.get(key);

        //then
        assertTrue(s.isPresent());
        assertEquals(key,s.get().getId());
    }

    @Test
    void addDuplicateKey(){
        //given
        String key = "sessionId";
        session.setId(key);
        Session duplicateSession = new Session(new Date(),new Date());
        duplicateSession.setId("duplicate");
        sessionStorage.add(key,session);

        //when
        sessionStorage.add(key,duplicateSession);
        Optional<Session> s = sessionStorage.get(key);

        //then
        assertTrue(s.isPresent());
        assertEquals("duplicate",s.get().getId());
    }
}