package codesquad.middleware;

import codesquad.application.model.User;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UserDatabase {
    public UserDatabase() {}
    private final static Map<String,User> store = new ConcurrentHashMap<>();

    public static void save(User user) {
        store.put(user.getId(),user);
    }

    public static Optional<User> findById(String userId) {
        return store.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(userId))
                .findFirst()
                .map(Map.Entry::getValue);
    }
}
