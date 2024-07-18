package codesquad.middleware.csv;

import codesquad.application.model.Board;
import codesquad.application.model.User;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.middleware.BoardDatabase;
import codesquad.middleware.DataSource;
import codesquad.was.http.exception.HttpInternalServerErrorException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Coffee
public class CsvBoardKeyDatabase {
    private final String url;
    public CsvBoardKeyDatabase(CsvInitializer initializer) {
        try {
            String initPath = initializer.init("board_key.csv", "boardId");
            Class.forName("codesquad.middleware.csv.driver.CsvDriver");
            url = initializer.getUrl(initPath);
        } catch (ClassNotFoundException e) {
            throw new HttpInternalServerErrorException("internal server error");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url);
    }

    public void save(Board board) {
        if(board == null) throw new IllegalArgumentException("board cannot be null");
        try(Connection con = getConnection()) {
            List<Board> all = findAll();
            long key = 1;
            if(all.size() != 1){
                key = all.get(all.size()-1).getBoardId()+1;
            }
            PreparedStatement pstm = con.prepareStatement("insert into \"BOARDKEY\"(boardId) values (?)");
            pstm.setLong(1,key);
            pstm.executeUpdate();

        } catch (SQLException e) {
            throw new HttpInternalServerErrorException("internal server error!");
        }
    }

    public List<Board> findAll() {
        try(Connection con = getConnection()){
            PreparedStatement ps = con.prepareStatement("select * from BOARD");

            ResultSet rs = ps.executeQuery();
            List<Board> boards = new ArrayList<>();
            while(rs.next()){
                Board board = boardMapper(rs);
                if(board != null){
                    boards.add(board);
                }
            }
            return boards;
        }catch (SQLException e) {
            throw new HttpInternalServerErrorException("internal server error!");
        }
    }

    public Optional<Board> findById(Long id) {
        try(Connection con = getConnection()){
            PreparedStatement ps = con.prepareStatement("select * from BOARD where boardId = ?");
            ps.setLong(1,id);
            ResultSet rs = ps.executeQuery();

            return Optional.ofNullable(rs.next() ? boardMapper(rs) : null);
        }catch (SQLException e) {
            throw new HttpInternalServerErrorException("internal server error!");
        }
    }

    private Board boardMapper(ResultSet rs) throws SQLException {
        String title = rs.getString("title");
        String content = rs.getString("content");
        String writer = rs.getString("writer");
        long boardId = rs.getLong("boardId");
        String path = rs.getString("path");
        return new Board(boardId, title, content, writer,path);
    }
}
