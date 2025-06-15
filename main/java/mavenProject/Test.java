package mavenProject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class Test {

	public static void main(String[] args) {
		String url = "jdbc:mysql://localhost:3306/newDB";
		
		try {
			Connection conn = DriverManager.getConnection(url, "root", "Avater12!");
			System.out.println("connected!!");
		} 
		catch(SQLException e) {
			System.out.println("Not Connected");
			e.printStackTrace();
		}

	}

}
