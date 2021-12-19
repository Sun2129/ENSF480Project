package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Creates (and holds) a connection to the database
public class DatabaseConnection {
	//Connection holder
    private static final Connection connection = makeConnection();
	
	//Create a connection to the database based on the parameters in the DatabaseConfig file
    private static Connection makeConnection() {
		//Try to make a connection
		try {
            return DriverManager.getConnection(DatabaseConfig.DB_URL, DatabaseConfig.DB_USER, DatabaseConfig.DB_PASSWORD);
        } catch (final SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	//Return the connection that is made
    public static Connection getConnection() {
        return connection;
    }
}
