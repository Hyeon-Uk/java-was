package codesquad.was.http.session;

import codesquad.framework.coffee.annotation.Coffee;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Coffee
public class SessionStorage {
    private final Map<String,Session> store;
    public SessionStorage(){
        store = new ConcurrentHashMap<>();
    }

    protected void add(String key,Session session){
        store.put(key,session);
    }

    protected Optional<Session> get(String sessionId){
        return Optional.ofNullable(store.get(sessionId));
    }
}
