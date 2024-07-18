package codesquad.middleware.csv;

import codesquad.application.model.Board;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.middleware.BoardDatabase;
import codesquad.was.http.exception.HttpInternalServerErrorException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Coffee
public class CsvBoardDatabase implements BoardDatabase {
    private final String url;
    private final String keyUrl;
    private final CsvInitializer initializer;

    public CsvBoardDatabase(CsvInitializer initializer) {
        try {
            this.initializer = initializer;
            String initPath = initializer.init("board.csv", "boardId,title,content,writer,path");
            keyUrl = initializer.init("board_key.csv", "boardId");
            Class.forName("codesquad.middleware.csv.driver.CsvDriver");
            url = initializer.getUrl(initPath);
        } catch (ClassNotFoundException e) {
            throw new HttpInternalServerErrorException("internal server error");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    private Connection getConnection(String url) throws SQLException {
        return DriverManager.getConnection(initializer.getUrl(url));
    }

    @Override
    public void save(Board board) {
        if (board == null) throw new IllegalArgumentException("board cannot be null");
        try (Connection con = getConnection()) {
            if (board.getBoardId() == null) {
                PreparedStatement ps = null;
                ps = getConnection(keyUrl).prepareStatement("select * from \"BOARDKEY\"");
                ResultSet rs = ps.executeQuery();
                long lastPk = 1;
                while (rs.next()) {
                    lastPk = rs.getLong(1);
                }
                ps = getConnection(keyUrl).prepareStatement("insert into \"BOARDKEY\"(boardId) values(?)");
                ps.setLong(1, lastPk+1);
                ps.executeUpdate();

                if (board.getImagePath() == null) {
                    ps = con.prepareStatement("insert into BOARD(boardId,title,content,writer) values (?,?,?,?)");
                    ps.setLong(1, lastPk);
                    ps.setString(2, board.getTitle());
                    ps.setString(3, board.getContent());
                    ps.setString(4, board.getWriter());
                } else {
                    ps = con.prepareStatement("insert into BOARD(boardId,title,content,writer,path) values (?,?,?,?,?)");
                    ps.setLong(1, lastPk);
                    ps.setString(2, board.getTitle());
                    ps.setString(3, board.getContent());
                    ps.setString(4, board.getWriter());
                    ps.setString(5, board.getImagePath());
                }

                int row = ps.executeUpdate();
                board.setBoardId(lastPk);
            } else {
                PreparedStatement ps = con.prepareStatement("insert into BOARD(boardId,title,content,writer) values (?,?,?,?)");
                ps.setLong(1, board.getBoardId());
                ps.setString(2, board.getWriter());
                ps.setString(3, board.getTitle());
                ps.setString(4, board.getContent());

                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new HttpInternalServerErrorException("internal server error!");
        }
    }

    @Override
    public List<Board> findAll() {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("select * from BOARD");

            ResultSet rs = ps.executeQuery();
            List<Board> boards = new ArrayList<>();
            while (rs.next()) {
                Board board = boardMapper(rs);
                if (board != null) {
                    boards.add(board);
                }
            }
            return boards;
        } catch (SQLException e) {
            throw new HttpInternalServerErrorException("internal server error!");
        }
    }

    @Override
    public Optional<Board> findById(Long id) {
        return findAll().stream()
                .filter(board->board.getBoardId().equals(id))
                .findFirst();
    }

    private Board boardMapper(ResultSet rs) throws SQLException {
        String title = rs.getString("title");
        String content = rs.getString("content");
        String writer = rs.getString("writer");
        long boardId = rs.getLong("boardId");
        String path = rs.getString("path");
        return new Board(boardId, title, content, writer, path);
    }
}
