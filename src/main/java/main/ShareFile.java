package main;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.JOptionPane;
 
public class ShareFile {
	
	public void sendMail(String mailer, String password, String receiver, String filename) {
		
		final String sender;
		final String pwd;
		
		sender = mailer;
		pwd = password;
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
 
		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(sender, pwd);
				}
			});
 
		try {
 
			FileDataSource fds = new FileDataSource(filename);
			
			MimeMessage message = new MimeMessage(session);
			
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(receiver));
			message.setDataHandler(new DataHandler(fds));
			message.setFileName(fds.getName());
			message.setSubject("File Share");
			
			// Create the message body part
	        BodyPart messageBodyPart = new MimeBodyPart();
	 
	        // Fill the message
	        messageBodyPart.setText("You've been shared " + fds.getName());
	          
	        // Create a multipart message for attachment
	        Multipart multipart = new MimeMultipart();
	 
	        // Set text message part
	        multipart.addBodyPart(messageBodyPart);
	 
	        // Second part is attachment
	        messageBodyPart = new MimeBodyPart();
	        DataSource source = new FileDataSource(filename);
	        messageBodyPart.setDataHandler(new DataHandler(source));
	        messageBodyPart.setFileName(fds.getName());
	        multipart.addBodyPart(messageBodyPart);
	 
	        // Send the complete message parts
	        message.setContent(multipart);
			
			Transport.send(message);
 
			JOptionPane.showConfirmDialog(null, "File Share Successful!", "Download file success",
					JOptionPane.PLAIN_MESSAGE);
 
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
