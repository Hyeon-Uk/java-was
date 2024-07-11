package codesquad.was.http.session;

import codesquad.was.http.message.request.HttpRequest;
import codesquad.was.http.message.response.HttpResponse;
import codesquad.was.utils.Timer;

import java.util.Optional;
import java.util.UUID;

public class SessionManager {
    private final Timer timer;
    private final SessionStorage sessionStorage;
    public SessionManager(SessionStorage sessionStorage,Timer timer) {
        this.sessionStorage = sessionStorage;
        this.timer = timer;
    }

    public Optional<Session> getSession(String sessionId){
        return sessionStorage.get(sessionId);
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
