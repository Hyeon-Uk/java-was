package codesquad.middleware;

import codesquad.application.model.User;
import codesquad.was.http.exception.HttpInternalServerErrorException;
import org.h2.mvstore.tx.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class H2UserDatabase implements UserDatabase{
    private final DataSource dataSource;
    public H2UserDatabase(DataSource dataSource) {
        this.dataSource = dataSource;
        try{
            Class.forName(dataSource.getDriverClassName());

            Connection con = getConnection();
            PreparedStatement pstm = con.prepareStatement("create table if not exists \"USER\"(userId varchar(255) primary key, password varchar(255), nickname varchar(255))");
            pstm.executeUpdate();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection(){
        try {
            return DriverManager.getConnection(dataSource.getUrl(),dataSource.getUsername(),dataSource.getPassword());
        } catch (SQLException e) {
            throw new HttpInternalServerErrorException("internal server error!");
        }
    }



    @Override
    public void save(User user) {
        Connection con = null;
        try {
            con = getConnection();
            con.setAutoCommit(false);
            PreparedStatement pstm1 = con.prepareStatement("select count(*) from \"USER\" where userId = ?");
            pstm1.setString(1,user.getId());
            ResultSet rs1 = pstm1.executeQuery();

            int count = 0;
            if(rs1.next()){
                count = rs1.getInt(1);
            }

            if(count == 0) {
                PreparedStatement ps = con.prepareStatement("insert into \"USER\"(userId,password,nickname) values (?,?,?)");
                ps.setString(1, user.getId());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getNickname());

                ps.executeUpdate();
            }
            else{
                PreparedStatement ps = con.prepareStatement("update \"USER\" set password = ? , nickname = ? where userId = ?");
                ps.setString(1, user.getPassword());
                ps.setString(2, user.getNickname());
                ps.setString(3, user.getId());

                ps.executeUpdate();
            }
            con.commit();
        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
            }
            e.printStackTrace();
            throw new HttpInternalServerErrorException("internal server error!");
        } finally{
            try {
                if(con!=null){
                    con.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public Optional<User> findById(String userId) {
        Connection con = null;
        try{
            con = getConnection();
            PreparedStatement ps = con.prepareStatement("select userId,password,nickname from \"USER\" where userId = ?");
            ps.setString(1,userId);

            ResultSet rs = ps.executeQuery();
            return Optional.ofNullable(rs.next() ? userMapper(rs) : null);
        }catch (SQLException e) {
            throw new HttpInternalServerErrorException("internal server error!");
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(Exception e){

            }
        }
    }

    @Override
    public List<User> findAll() {
        Connection con = null;
        try{
            con = getConnection();
            PreparedStatement ps = con.prepareStatement("select userId,password,nickname from \"USER\"");

            ResultSet rs = ps.executeQuery();
            List<User> users = new ArrayList<>();
            while(rs.next()){
                User user = userMapper(rs);
                if(user!=null){
                    users.add(user);
                }
            }
            return users;
        }catch (SQLException e) {
            throw new HttpInternalServerErrorException("internal server error!");
        }finally{
            try{
                if(con!=null){
                    con.close();
                }
            }catch(Exception e){

            }
        }
    }

    private User userMapper(ResultSet rs){
        try {
            String id = rs.getString("userId");
            String password = rs.getString("password");
            String nickname = rs.getString("nickname");
            return new User(id, password, nickname);
        }catch(SQLException e){
            return null;
        }
    }
}
