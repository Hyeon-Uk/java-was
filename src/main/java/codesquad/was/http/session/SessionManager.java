package codesquad.was.http.session;

import codesquad.framework.coffee.annotation.Coffee;
import codesquad.was.utils.Timer;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Coffee
public class SessionManager {
    private final Timer timer;
    private final SessionStorage sessionStorage;
    public SessionManager(SessionStorage sessionStorage,Timer timer) {
        this.sessionStorage = sessionStorage;
        this.timer = timer;
    }

    public Optional<Session> getSession(String sessionId){
        return sessionStorage.get(sessionId)
                .filter(session->!session.isExpired()&&timer.getCurrentTime().before(new Date(session.getCreatedAt().getTime()+session.getMaxInactiveInterval()*1000)))
                .map(session->{
                    session.setLastAccessedAt(timer.getCurrentTime());
                    return session;
                });
    }

    public Session createSession(){
        String id = generateSessionId();
        Session session = new Session(timer.getCurrentTime(),timer.getCurrentTime());
        session.setId(id);
        sessionStorage.add(id,session);
        return session;
    }

    private String generateSessionId(){
        return UUID.randomUUID().toString();
    }

}
