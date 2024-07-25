package codesquad.middleware;

import codesquad.application.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDatabase {
    void save(User user);

    Optional<User> findById(String userId);

    List<User> findAll();
}
