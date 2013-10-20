import java.sql.Connection;

/* This class acts like worker whom Thread pool executer will send work to
 * This class implement the Runnable interface of java.*/
public class WorkerThread implements Runnable {
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

		Connection connection = Database.startConnection();  // start connection
		Database.updateDataInTable(connection, id);          // Update in table sent=true after every email is sent by a thread.
		Database.closeConnection(connection);                // Close connection
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
