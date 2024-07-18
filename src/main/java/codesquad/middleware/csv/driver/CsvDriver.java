package codesquad.middleware.csv.driver;

import codesquad.middleware.csv.connection.CsvConnection;
import codesquad.was.http.exception.HttpInternalServerErrorException;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class CsvDriver implements Driver {

    static {
        try {
            DriverManager.registerDriver(new CsvDriver());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to register CSV JDBC driver", e);
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (acceptsURL(url)) {
            String csvPath = url.replace("jdbc:csv:", "");
            return new CsvConnection(csvPath);
        }
        return null;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url.startsWith("jdbc:csv:");
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 1;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public java.util.logging.Logger getParentLogger() {
        return null;
    }
}
