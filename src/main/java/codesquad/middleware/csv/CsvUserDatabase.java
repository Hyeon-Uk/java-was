package codesquad.middleware.csv;

import codesquad.application.model.User;
import codesquad.framework.coffee.annotation.Coffee;
import codesquad.framework.coffee.annotation.Named;
import codesquad.middleware.DataSource;
import codesquad.middleware.UserDatabase;
import codesquad.was.http.exception.HttpInternalServerErrorException;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Coffee
public class CsvUserDatabase implements UserDatabase {
    private final String url;
    public CsvUserDatabase(CsvInitializer initializer) {
        try {
            String initPath = initializer.init("user.csv", "userId,nickname,password");
            Class.forName("codesquad.middleware.csv.driver.CsvDriver");
            url = initializer.getUrl(initPath);
        } catch (ClassNotFoundException e) {
            throw new HttpInternalServerErrorException("internal server error");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new CsvUserDatabase(new CsvInitializer());
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }


    @Override
    public void save(User user) {
        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try {
                PreparedStatement ps = con.prepareStatement("insert into \"USER\"(userId,nickname,password) values (?, ?, ? )");
                ps.setString(1, user.getId());
                ps.setString(2, user.getNickname());
                ps.setString(3, user.getPassword());

                ps.executeUpdate();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
            con.commit();
        } catch (SQLException e) {
            throw new HttpInternalServerErrorException("internal server error!");
        }
    }

    @Override
    public Optional<User> findById(String userId) {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("select * from \"USER\"");

            ResultSet rs = ps.executeQuery();
            List<User> users = new ArrayList<>();
            while(rs.next()){
                users.add(userMapper(rs));
            }
            return users.stream().filter(user->user.getId().equals(userId)).findFirst();
        } catch (SQLException e) {
            throw new HttpInternalServerErrorException("internal server error!");
        }
    }

    @Override
    public List<User> findAll() {
        try (Connection con = getConnection()) {
            PreparedStatement ps = con.prepareStatement("select * from \"USER\"");

            ResultSet rs = ps.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                System.out.println("next!");
                User user = userMapper(rs);
                if (user != null) {
                    users.add(user);
                }
            }
            return users;
        } catch (SQLException e) {
            throw new HttpInternalServerErrorException("internal server error!");
        }
    }

    private User userMapper(ResultSet rs) throws SQLException {
        String id = rs.getString("userId");
        String password = rs.getString("password");
        String nickname = rs.getString("nickname");
        return new User(id, password, nickname);
    }
}
