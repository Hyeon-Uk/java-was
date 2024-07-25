package codesquad.was.http.session;

import codesquad.message.mock.MockTimer;
import codesquad.was.utils.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {
    private MockTimer timer;
    private SessionStorage storage;
    private SessionManager manager;

    @BeforeEach
    void setUp(){
        timer = new MockTimer(1000l);
        storage = new SessionStorage();
        manager = new SessionManager(storage,timer);
    }

    @Test
    void createSession(){
        //given

        //when
        Session session = manager.createSession();

        //then
        assertNotNull(session.getId());
        assertEquals(timer.getCurrentTime(),session.getCreatedAt());
        assertEquals(timer.getCurrentTime(),session.getLastAccessedAt());
    }

    @Test
    void getSession_lastAccessedAt_update(){
        //given
        Session session = manager.createSession();
        String key = session.getId();
        long beforeTime = timer.getCurrentTime().getTime();
        long accessTime = timer.getCurrentTime().getTime()+1000;

        timer.setTime(accessTime);

        //when
        Optional<Session> s = manager.getSession(key);

        //then
        assertTrue(s.isPresent());
        assertEquals(beforeTime,s.get().getCreatedAt().getTime());
        assertEquals(accessTime,s.get().getLastAccessedAt().getTime());
    }

    @Test
    void getSession_expired_session(){
        //given
        Session session = manager.createSession();
        session.setMaxInactiveIntervalSeconds(0);
        String key = session.getId();
        timer.setTime(timer.getCurrentTime().getTime()+1);

        //when
        Optional<Session> s = manager.getSession(key);

        //then
        assertTrue(s.isEmpty());
    }

    @Test
    void getSession_invalidated_session(){
        //given
        Session session = manager.createSession();
        session.invalidate();
        String key = session.getId();

        //when
        Optional<Session> s = manager.getSession(key);

        //then
        assertTrue(s.isEmpty());
    }
}