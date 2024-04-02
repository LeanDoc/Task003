package connection;

import org.testcontainers.shaded.com.trilead.ssh2.Connection;

import java.sql.SQLException;

public interface ConnectionManager {
    Connection getConnection() throws SQLException;
}
