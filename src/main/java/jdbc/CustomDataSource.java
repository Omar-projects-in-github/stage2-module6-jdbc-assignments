package jdbc;

import javax.sql.DataSource;

import lombok.Getter;
import lombok.Setter;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

@Getter
@Setter
public class CustomDataSource implements DataSource {
    private static volatile CustomDataSource instance;
    private final String driver = "org.postgresql.Driver";
    private final String url = "jdbc:postgresql://localhost:5432/myfirstdb";
    private final String name = "postgres";
    private final String password = "postgresql";

    private PrintWriter logWriter = null;
    private int loginTimeout = 0;

    private CustomDataSource() {
        try {
            Class.forName(driver);
        }
        catch (ClassNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    public static CustomDataSource getInstance() {
        if (instance == null) {
            synchronized (CustomDataSource.class) {
                if (instance == null) {
                    instance = new CustomDataSource();
                }
            }
        }
        return instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return new CustomConnector().getConnection(url, name, password);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return new CustomConnector().getConnection(url, username, password);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return logWriter;
    }
    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        logWriter = out;
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return loginTimeout;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        loginTimeout = seconds;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
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
