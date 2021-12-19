package util;

//Used to establish a connection to the database
class DatabaseConfig {
	//Holds where the database is stored
    public static final String DB_URL = "jdbc:mysql://localhost:3306/rentings?connectTimeout=0&socketTimeout=0&autoReconnect=true";
	//Username of the user
    public static final String DB_USER = "root";
	//Password of the user
    public static final String DB_PASSWORD = "Sanchit2001!";
}
