package codesquad.middleware;

import codesquad.application.model.Board;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.was.http.exception.HttpInternalServerErrorException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class H2BoardDatabase implements BoardDatabase{
    private final DataSource dataSource;
    public H2BoardDatabase(DataSource dataSource){
        this.dataSource = dataSource;
        Connection con=null;
        try{
            Class.forName(dataSource.getDriverClassName());
            con = DriverManager.getConnection(dataSource.getUrl(),dataSource.getUsername(),dataSource.getPassword());
            con.setAutoCommit(false);
            con.prepareStatement("drop table if exists \"BOARD\"").executeUpdate();

            PreparedStatement pstm = con.prepareStatement("create table  \"BOARD\"(boardId bigint primary key AUTO_INCREMENT, title varchar(255), content text,writer varchar(255),path varchar(255))");
            pstm.executeUpdate();

            con.commit();
        } catch (ClassNotFoundException | SQLException e) {
            try {
                if(con!=null) {
                    con.rollback();
                }
            } catch (SQLException ex) {
            }
            throw new HttpInternalServerErrorException("internal server error!");
        }
    }

    private Connection getConnection() throws SQLException{
        return DriverManager.getConnection(dataSource.getUrl(),dataSource.getUsername(),dataSource.getPassword());
    }

    @Override
    public void save(Board board) {
        if(board == null) throw new IllegalArgumentException("board cannot be null");
        try(Connection con = getConnection()) {
            con.setAutoCommit(false);
            try {
                int count = 0;

                if (board.getBoardId() != null) {
                    PreparedStatement pstm1 = con.prepareStatement("select count(*) from BOARD where boardId = ?");
                    pstm1.setLong(1, board.getBoardId());
                    ResultSet rs1 = pstm1.executeQuery();

                    if (rs1.next()) {
                        count = rs1.getInt(1);
                    }

                }
                if (count == 0) {
                    if (board.getBoardId() == null) {
                        PreparedStatement ps = null;
                        if (board.getImagePath() == null) {
                            ps = con.prepareStatement("insert into BOARD(writer,title,content) values (?,?,?)", Statement.RETURN_GENERATED_KEYS);
                            ps.setString(1, board.getWriter());
                            ps.setString(2, board.getTitle());
                            ps.setString(3, board.getContent());
                        } else {
                            ps = con.prepareStatement("insert into BOARD(writer,title,content,path) values (?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                            ps.setString(1, board.getWriter());
                            ps.setString(2, board.getTitle());
                            ps.setString(3, board.getContent());
                            ps.setString(4, board.getImagePath());
                        }

                        int row = ps.executeUpdate();
                        if (row > 0) {
                            ResultSet generatedKeys = ps.getGeneratedKeys();
                            if (generatedKeys.next()) {
                                long key = generatedKeys.getLong(1);
                                board.setBoardId(key);
                            }
                        }
                    } else {
                        PreparedStatement ps = con.prepareStatement("insert into BOARD(boardId,writer,title,content) values (?,?,?,?)");
                        ps.setLong(1, board.getBoardId());
                        ps.setString(2, board.getWriter());
                        ps.setString(3, board.getTitle());
                        ps.setString(4, board.getContent());

                        ps.executeUpdate();
                    }
                } else {
                    PreparedStatement ps = con.prepareStatement("update BOARD set writer = ? , title = ?, content = ? where boardId = ?");
                    ps.setString(1, board.getWriter());
                    ps.setString(2, board.getTitle());
                    ps.setString(3, board.getContent());
                    ps.setLong(4, board.getBoardId());

                    ps.executeUpdate();
                }
            }catch(SQLException e){
                con.rollback();
                throw e;
            }
            con.commit();
        } catch (SQLException e) {
            throw new HttpInternalServerErrorException("internal server error!");
        }
    }

    @Override
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

    @Override
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
