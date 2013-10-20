import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mysql.jdbc.PreparedStatement;

/* This class handles all Database related stuff */
public class Utility {

	/* Function to start connection with database */
	public static Connection startConnection() {
		Connection connection = null; // initialize a variable connection of
										// type Connection to NULL
		try {
			Class.forName("com.mysql.jdbc.Driver"); // JDBC driver for MySql
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {

			/*
			 * here we pass the parameters to connect to Database
			 * emails","sqluser","sqluserpw"    ->  "emails" is the database name
			 *                                  ->  "sqluser" is the username for the Mysql Database 
			 *                                  ->  "sqluserpw" is the password for the MySql Database
			 */
			connection = DriverManager.getConnection(
					"jdbc:mysql://localhost:3307/emails", "sqluser",
					"sqluserpw");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connection; // return connection variable.
	}

	/* Function to close connection with the Database. */
	public static void closeConnection(Connection connection) {
		try {
			connection.close(); // close the connection.
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * This function fetches specified number of records from the table we
	 * cannot fetch all the records in one go since Database can have millions
	 * of records so we can limit our SELECT query to fetch only limited
	 * records.
	 */
	public static ResultSet getDataFromTable(Connection connection) {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultSet rs = null;
		try {

			/*
			 * Select all columns from table EmailQueue where sent is false
			 * Limited to 100 records only
			 */
			rs = stmt.executeQuery("select * from EmailQueue where sent='false' LIMIT 100");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs; // Return the resultset rs.
	}
    
	
	/* This function updates the status of all the emails that are successfully sent from sent=false to sent=true
	 * so that no email is sent twice by any other thread. */
	public static void updateDataInTable(Connection connection, int id) {
		java.sql.PreparedStatement stmt = null;
		try {
			String sqlQuery = "Update EmailQueue SET sent=true where id=?";  // Update table EmailQueue and set sent=true for every id that the function gets as argument.
			stmt = connection.prepareStatement(sqlQuery);
			stmt.setInt(1, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
