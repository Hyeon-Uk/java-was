package codesquad.middleware;

import codesquad.application.model.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class H2BoardDatabaseTest {
    private DataSource dataSource = new MyH2DataSource();
    private BoardDatabase database = new H2BoardDatabase(dataSource);

    @BeforeEach
    void setUp(){
        try{
            Class.forName(dataSource.getDriverClassName());

            Connection con = DriverManager.getConnection(dataSource.getUrl(),dataSource.getUsername(),dataSource.getPassword());
            PreparedStatement ps = con.prepareStatement("delete from \"BOARD\"");
            ps.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nested
    @DisplayName("save")
    class SaveTest {
        @Test
        void saveAndFindByIdTest() {
            //given
            String title = "title";
            String content = "content";
            String writer = "writer";
            Board board = new Board(Long.valueOf(1l), title, content, writer);

            //when
            database.save(board);

            //then
            Optional<Board> byId = database.findById(board.getBoardId());
            assertTrue(byId.isPresent());
            assertEquals(board,byId.get());
        }

        @Test
        void saveWithNull(){
            //given

            //when & then
            assertThrows(IllegalArgumentException.class, () -> database.save(null));
        }

        @Test
        void saveWithoutBoardId() {
            //given
            String title = "title";
            String content = "content";
            String writer = "writer";
            Board board = new Board(null, title, content, writer);

            //when
            database.save(board);

            //then
            assertNotNull(board.getBoardId());
            Optional<Board> byId = database.findById(board.getBoardId());
            assertTrue(byId.isPresent());
            assertEquals(board,byId.get());
        }

        @Test
        void saveDuplicatedBoard() {
            //given
            String title = "title";
            String content = "content";
            String writer = "writer";
            Board board = new Board(Long.valueOf(1l), title, content, writer);
            database.save(board);

            Board duplicated = new Board(Long.valueOf(1l), "duplicatedTitle", "duplicatedContent", writer);

            //when
            database.save(duplicated);

            //then
            Optional<Board> byId = database.findById(duplicated.getBoardId());
            assertTrue(byId.isPresent());
            assertEquals(duplicated,byId.get());
        }
    }



    @Nested
    @DisplayName("find All")
    class FindAllTest {
        @Test
        void findAllTest(){
            //given
            Board board1 = new Board(Long.valueOf(1l),"title1","content1","writer1");
            Board board2 = new Board(Long.valueOf(2l),"title2","content2","writer2");
            Board board3 = new Board(Long.valueOf(3l),"title3","content3","writer3");
            database.save(board1);
            database.save(board2);
            database.save(board3);

            //when
            List<Board> all = database.findAll();

            //then
            assertTrue(all.containsAll(List.of(board1,board2,board3)));
        }
    }

    @Nested
    @DisplayName("find by id")
    class FindByIdTest {
        @Test
        void findByIdTest(){
            //given
            Board board1 = new Board(Long.valueOf(1l),"title1","content1","writer1");
            database.save(board1);

            //when
            Optional<Board> byId = database.findById(board1.getBoardId());

            //then
            assertTrue(byId.isPresent());
            assertEquals(board1, byId.get());
        }
        @Test
        void findByIdWithNotExists(){
            //given
            Long id = Long.MAX_VALUE;

            //when
            Optional<Board> byId = database.findById(id);

            //then
            assertFalse(byId.isPresent());
        }
    }
}