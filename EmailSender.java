import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/* This class acts like worker whom Thread pool executer will send work to
 * This class implement the Runnable interface of java.*/
class WorkerThread implements Runnable {
	private String from_address;
	private String to_address;
	private String subject;
	private String body;
	private int id;

		public WorkerThread(String from_address,String to_address,String subject,String body, int id) {
		this.from_address= from_address;
		this.to_address= to_address;
		this.subject= subject;
		this.body= body;
		this.id = id;
	}

	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName()+ "Start. Record id = " + id );
		sendEmail(id,from_address,to_address,subject,body);
		System.out.println(Thread.currentThread().getName() + "End.");

		Connection connection = Utility.startConnection();  // start connection
		Utility.updateDataInTable(connection, id);          // Update in table sent=true after every email is sent by a thread.
		Utility.closeConnection(connection);                // Close connection
	}

	private void sendEmail(int id, String from_address, String to_address, String subject, String body) {
		try {// TODO code for sending mail is commented so that the code can be tested.
/*	    	String host="mail.xyz.com.com";
	    	String user=from_address;//change accordingly
	    	String password="passwordxyz";//change accordingly
	    	
	    	String to=to_address;//change accordingly
	    	Properties props = new Properties();
			props.put("mail.smtp.host", host);
			props.put("mail.smtp.auth", "true");
			Session session = Session.getDefaultInstance(props,
					new javax.mail.Authenticator() {
						protected PasswordAuthentication getPasswordAuthentication() {
							return new PasswordAuthentication(user,
									password);
						}
					});
				// Compose the message
			try {
				MimeMessage msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress(user));
				msg.addRecipient(Message.RecipientType.TO,
						new InternetAddress(to));
				msg.setSubject(subject);
				msg.setText(body);
				// send the message
				Transport.send(msg);

				} catch (MessagingException e) {
					e.printStackTrace();
				}
				System.out.println("message sent successfully...");*/
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	
}

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
		Connection connection = Utility.startConnection(); 
		
		/* This while loop is started for infinite times so that it can keep on fetching a particular number of records 
		 * and keep sending them to the thread pool executer. As long as the resultset is getting records it will continue
		 * and as soon as there are no more records left unsent the while loop will break indicating that ALL EMAILS ARE SENT */
		while(true){
		/* Fetch records from the table by calling getDataFromTable() function of Utility class. */
		ResultSet rs = Utility.getDataFromTable(connection);
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
		Utility.closeConnection(connection); // Close the Database connection
	}
}
