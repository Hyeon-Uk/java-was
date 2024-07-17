package codesquad.application.handler.mock;

import codesquad.application.model.Board;
import codesquad.middleware.BoardDatabase;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MockBoardDatabase implements BoardDatabase {
    private final Map<Long, Board> store = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(0);
    @Override
    public void save(Board board) {
        if(board==null) throw new IllegalArgumentException("Board Id is null");
        if(board.getBoardId() == null){
            board.setBoardId(seq.incrementAndGet());
        }
        store.put(board.getBoardId(),board);
    }

    @Override
    public List<Board> findAll() {
        return store.values().stream()
                .toList();
    }

    @Override
    public Optional<Board> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }
}
