package codesquad.was.http.session;

import codesquad.message.mock.MockTimer;
import codesquad.was.utils.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 세션에 Key,Value로 Object를 저장할 수 있다. v
 * 세션에서 Key 값을 이용하여 Object를 가져올 수 있다. v
 * 세션에서 Key값을 이용하여 Object를 삭제할 수 있다. v
 * 같은 Key값을 이용하여 저장하면 이후에 저장된 객체가 사용됨 v
 * 없는 Key값이면 그냥 그러려니 한다. v
 * 기본 timeout 값은 30분 v
 * 세션의 시간을 만료시킬 수 있다.
 * timeout이 된 session의 객체는 사라짐
 * 동시에 접근 및 조회를 진행하면 순차적으로 처리되어야함
 * 가장 최근에 접근한 시간을 조회할 수 있어야함
 * 생성된 시간을 조회할 수 있어야함
 */
class SessionTest {
    private class Person{
        String name;
        int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    private Session session;
    private Timer timer;
    private long currentTime = 1l;
    @BeforeEach
    void setUp(){
        timer = new MockTimer(currentTime);
        session = new Session(timer.getCurrentTime(),timer.getCurrentTime());
    }

    @Test
    void sessionDefaultValue(){
        //given

        //when
        long activeInterval = session.getMaxInactiveInterval();

        //then
        assertEquals(30*60,activeInterval);
    }

    @Test
    void constructorWithMaxInactiveInterval(){
        //given
        long givenInterval = 1000l;
        session = new Session(givenInterval,timer.getCurrentTime(),timer.getCurrentTime());

        //when
        long activeInterval = session.getMaxInactiveInterval();
        //then
        assertEquals(givenInterval,activeInterval);
    }

    @Test
    void sessionSaveAndGetTest(){
        //given
        String key = "Key";
        Person value = new Person("Kim",27);

        //when
        session.set(key,value);

        //then
        Optional<Object> ret = session.get(key);
        assertTrue(ret.isPresent());
        assertEquals(value,(Person)ret.get());
    }

    @Test
    void duplicatedKeyTest(){
        //given
        String key = "Key";
        Person beforeValue = new Person("before",1);
        Person afterValue = new Person("after",2);
        session.set(key,beforeValue);

        //when
        session.set(key,afterValue);

        //then
        assertTrue(session.get(key).isPresent());
        assertEquals(afterValue,session.get(key).get());
    }

    @Test
    void sessionRemoveTest(){
        //given
        String key = "Key";
        Person value = new Person("Kim",27);
        session.set(key,value);

        //when
        session.remove(key);

        //then
        assertTrue(session.get(key).isEmpty());
    }

    @Test
    void sessionRemoveNothingTest(){
        //given
        String key = "NotExist";

        //when
        session.remove(key);

        //then
        assertTrue(session.get(key).isEmpty());
    }

    @Test
    void sessionCreatedDateAndLastAccessTest(){
        //given

        //when
        Date createdAt = session.getCreatedAt();
        Date lastAccess = session.getLastAccessedAt();

        //then
        assertEquals(currentTime,createdAt.getTime());
        assertEquals(currentTime,lastAccess.getTime());
    }

    @Test
    void sessionCreatedDateAndLastAccessTest2(){
        //given
        long lastUpdated = 100l;

        //when
        session.setLastAccessedAt(new Date(lastUpdated));

        //then
        assertEquals(lastUpdated,session.getLastAccessedAt().getTime());
    }
}