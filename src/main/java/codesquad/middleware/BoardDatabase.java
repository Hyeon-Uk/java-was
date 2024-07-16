package codesquad.middleware;

import codesquad.application.model.Board;

import java.util.List;
import java.util.Optional;

public interface BoardDatabase {
    void save(Board board);
    List<Board> findAll();
    Optional<Board> findById(Long id);
}
