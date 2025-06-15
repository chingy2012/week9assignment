package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import projects.exception.DbException;

public class DbConnection {
	
	 // Database connection configuration
	
	private static String HOST = "localhost";
	private static String PASSWORD = "projects";
	private static int PORT = 3306;
	private static String SCHEMA = "projects";
	private static String USER = "projects";
	  /**
     * Establishes and returns a connection to the database.
     *
     * @return A valid {@link Connection} object.
     * @throws DbException if the connection cannot be established.
     */
	public static Connection getConnection() {
		 // Format the JDBC URI using the provided connection details
		String uri = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s&useSSL=false",
				HOST, PORT, SCHEMA, USER, PASSWORD);
				
		try {
			 // Attempt to establish a connection to the database
			Connection conn = DriverManager.getConnection(uri);
			System.out.println("Connection successful!");// Log success message
			return conn;
		} catch(SQLException e) {
			  // Log the connection URI and throw a custom database exception if connection fails
			System.out.println("Unable to get connection at " + uri);
			throw new DbException("Unable to connect to the database.", e);
		}
	}

}
