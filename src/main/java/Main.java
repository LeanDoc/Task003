import connection.ConnectionManagerImpl;
import util.InitSqlScheme;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        System.out.println("Hello world!");
        ConnectionManagerImpl connectionManager = ConnectionManagerImpl.getInstance();
        InitSqlScheme.initSqlScheme(connectionManager);
        InitSqlScheme.initSqlData(connectionManager);
    }
}
