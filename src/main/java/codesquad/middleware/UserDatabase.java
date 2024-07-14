package codesquad.middleware;

import codesquad.application.model.User;
import codesquad.framework.coffee.annotation.Coffee;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Coffee
public class UserDatabase {
    public UserDatabase() {}
    private final static Map<String,User> store = new ConcurrentHashMap<>();

    public void save(User user) {
        store.put(user.getId(),user);
    }

    public Optional<User> findById(String userId) {
        return store.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(userId))
                .findFirst()
                .map(Map.Entry::getValue);
    }

    public List<User> findAll(){
        return store.values()
                .stream()
                .toList();
    }
}
