package codesquad.application.handler.mock;

import codesquad.application.model.User;
import codesquad.middleware.UserDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MockUserDatabase implements UserDatabase {
    private final Map<String, User> store = new HashMap<>();
    @Override
    public void save(User user) {
        store.put(user.getId(),user);
    }

    @Override
    public Optional<User> findById(String userId) {
        return Optional.ofNullable(store.get(userId));
    }

    @Override
    public List<User> findAll() {
        return store.values().stream().toList();
    }
}
