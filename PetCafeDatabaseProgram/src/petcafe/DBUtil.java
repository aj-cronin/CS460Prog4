package petcafe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private static final String URL =
            "jdbc:oracle:thin:@aloe.cs.arizona.edu:1521:oracle";

    public static Connection getConnection(String user, String pass)
            throws SQLException {

        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC driver not found.");
            throw new SQLException(e);
        }

        return DriverManager.getConnection(URL, user, pass);
    }
}
