package main;

import java.io.File;
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

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
 
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
	public void sendFile(String filename) throws Exception {
		 
		CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost("https://api.teknik.io/upload/post");

            FileBody bin = new FileBody(new File(filename));

            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", bin).build();


            httppost.setEntity(reqEntity);

            System.out.println("executing request " + httppost.getRequestLine());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    System.out.println("Response content length: " + resEntity.getContentLength());
                }
                System.out.println(EntityUtils.toString(resEntity));
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
	}
}
