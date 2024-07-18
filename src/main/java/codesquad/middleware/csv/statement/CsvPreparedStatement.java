package codesquad.middleware.csv.statement;

import codesquad.middleware.csv.CsvResultSet;
import codesquad.middleware.csv.connection.CsvConnection;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CsvPreparedStatement implements PreparedStatement {
    private String sql;
    private final CsvConnection con;
    private String[] parameters;
    public CsvPreparedStatement(CsvConnection con,String sql) {
        this.con = con;
        this.sql = sql;
        this.parameters = new String[countParameters(sql)];
    }

    private int countParameters(String sql){
        int count = 0;
        for (char c : sql.toCharArray()) {
            if(c == '?') count++;
        }
        return count;
    }

    private String resolveSql(){
        StringBuilder sb = new StringBuilder(sql);
        int paramIndex = 0;
        int fromIndex = 0;

        while ((fromIndex = sb.indexOf("?", fromIndex)) != -1) {
            if (paramIndex >= parameters.length) {
                throw new IllegalArgumentException("Number of parameters does not match number of placeholders");
            }
            sb.replace(fromIndex, fromIndex + 1, parameters[paramIndex]);
            fromIndex += parameters[paramIndex].length() + 1;
            paramIndex++;
        }

        // Check if there are leftover parameters
        if (paramIndex != parameters.length) {
            throw new IllegalArgumentException("Number of parameters does not match number of placeholders");
        }

        return sb.toString();
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        if(!sql.toLowerCase().startsWith("select")){
            throw new SQLException("Can not execute query because the sql doesn't start with select");
        }
        sql = resolveSql();
        String path = con.getCsvPath();
        try (BufferedReader br = new BufferedReader(new FileReader(path))){
            List<String[]> rows = new LinkedList<>();
            String line = null;
            while((line = br.readLine()) != null){
                rows.add(line.split(","));
            }
            return new CsvResultSet(rows);
        }catch(IOException e){
            throw new SQLException("Error reading csv file", e.getMessage());
        }
    }

    @Override
    public int executeUpdate() throws SQLException {
        sql = resolveSql();
        if(sql.toLowerCase().startsWith("insert")){
            String[] values = Arrays.stream(sql.substring(sql.toLowerCase().indexOf("values")+"values".length()+1)
                    .replace("(","")
                    .replace(")","")
                    .split(","))
                    .map(String::trim)
                    .toArray(String[]::new);

            try(FileWriter fw = new FileWriter(con.getCsvPath(),true)){
                fw.write(String.join(",",values).concat("\n"));

                return 1;
            }catch(IOException e){
                throw new SQLException("error");
            }
        }
        throw new SQLException("can't");
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        parameterIndex--;
        if(parameterIndex<0 || parameterIndex >= parameters.length) throw new SQLException("you could not set this index "+(parameterIndex+1));
        parameters[parameterIndex] = null;
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        parameterIndex--;
        if(parameterIndex<0 || parameterIndex >= parameters.length) throw new SQLException("you could not set this index "+(parameterIndex+1));
        parameters[parameterIndex] = String.valueOf(x);
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        parameterIndex--;
        if(parameterIndex<0 || parameterIndex >= parameters.length) throw new SQLException("you could not set this index "+(parameterIndex+1));
        parameters[parameterIndex] = String.valueOf(x);
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        parameterIndex--;
        if(parameterIndex<0 || parameterIndex >= parameters.length) throw new SQLException("you could not set this index "+(parameterIndex+1));
        parameters[parameterIndex] = String.valueOf(x);
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        parameterIndex--;
        if(parameterIndex<0 || parameterIndex >= parameters.length) throw new SQLException("you could not set this index "+(parameterIndex+1));
        parameters[parameterIndex] = String.valueOf(x);
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        parameterIndex--;
        if(parameterIndex<0 || parameterIndex >= parameters.length) throw new SQLException("you could not set this index "+(parameterIndex+1));
        parameters[parameterIndex] = String.valueOf(x);
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        parameterIndex--;
        if(parameterIndex<0 || parameterIndex >= parameters.length) throw new SQLException("you could not set this index "+(parameterIndex+1));
        parameters[parameterIndex] = String.valueOf(x);
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        parameterIndex--;
        if(parameterIndex<0 || parameterIndex >= parameters.length) throw new SQLException("you could not set this index "+(parameterIndex+1));
        parameters[parameterIndex] = String.valueOf(x);
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {

    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        parameterIndex--;
        if(parameterIndex<0 || parameterIndex >= parameters.length) throw new SQLException("you could not set this index "+(parameterIndex+1));
        parameters[parameterIndex] = x;
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {

    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {

    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {

    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {

    }

    @Override
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {

    }

    @Override
    public void clearParameters() throws SQLException {

    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {

    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {

    }

    @Override
    public boolean execute() throws SQLException {

        return false;
    }

    @Override
    public void addBatch() throws SQLException {

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {

    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {

    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {

    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {

    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {

    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {

    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {

    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {

    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {

    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {

    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {

    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {

    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {

    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {

    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {

    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {

    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {

    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {

    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {

    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {

    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {

    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {

    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {

    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {

    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        return 0;
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {

    }

    @Override
    public int getMaxRows() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {

    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {

    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {

    }

    @Override
    public void cancel() throws SQLException {

    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public void setCursorName(String name) throws SQLException {

    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return null;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {

    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {

    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return 0;
    }

    @Override
    public void addBatch(String sql) throws SQLException {

    }

    @Override
    public void clearBatch() throws SQLException {

    }

    @Override
    public int[] executeBatch() throws SQLException {
        return new int[0];
    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return 0;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {

    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {

    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
