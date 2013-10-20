import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class EmailSender {

	public static void mailSender(ResultSet temp) throws SQLException {
		
		/* Start the executer service with a fixed thread pool of 50 threads.
		 * Number of threads can be increased on decreased depending on the system 
		 * on which this code is running */
		ExecutorService executor = Executors.newFixedThreadPool(50); 
		/* Iterate over the resultset that was sent by main program. */
		while (temp.next()) {

			/*
			 * Using Runnable interface define workers and pass the work to be
			 * done by each worker.
			 */
			Runnable worker = new WorkerThread(temp.getString("from_email_address"),temp.getString("to_email_address"),temp.getString("subject"),temp.getString("body"),temp.getInt("id"));
			executor.execute(worker); // Ask executer to execute each worker.
		}
		executor.shutdown(); // As soon as iteration is complete stop the executer.

		/* till the time threads are still working keep this while loop rolling */
		while (!executor.isTerminated()) {
		}

	}

	/* Main method */

	public static void main(String[] args) throws ClassNotFoundException,SQLException {
        
		/* Start connection with the Database */
		Connection connection = Database.startConnection(); 
		
		/* This while loop is started for infinite times so that it can keep on fetching a particular number of records 
		 * and keep sending them to the thread pool executer. As long as the resultset is getting records it will continue
		 * and as soon as there are no more records left unsent the while loop will break indicating that ALL EMAILS ARE SENT */
		while(true){
		/* Fetch records from the table by calling getDataFromTable() function of Utility class. */
		ResultSet rs = Database.getDataFromTable(connection);
		ResultSet temp= rs;
		if(!rs.isBeforeFirst()){
			System.out.println("All emails sent");
			break;
		}
		else{
		mailSender(temp); // Send this resultset of records to mailSender()
						// function where it will be given to a predefined pool
						// of threads.
		}
		}
		Database.closeConnection(connection); // Close the Database connection
	}
}
