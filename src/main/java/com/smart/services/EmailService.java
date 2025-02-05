package com.smart.services;


import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	 public boolean sendEmail(String subject, String message, String to){

	        //rest of code
	        boolean f = false;

	        String from = "dipak1657445@gmail.com";

	        //Variable for Email
	        String host="smtp.gmail.com";

	        //get the system properties
	        Properties properties = System.getProperties();
	        System.out.println("PROPERTIES "+properties);

	        //setting important information to properties object

	        //host set
	        properties.put("mail.smtp.host", host);
	        properties.put("mail.smtp.port", "465");
	        properties.put("mail.smtp.ssl.enable", "true");
	        properties.put("mail.smtp.auth", "true");

	        //Step 1: to get the session object.
	        Session session = Session.getInstance(properties, new Authenticator() {
	        	@Override
	         protected PasswordAuthentication getPasswordAuthentication() {
	       
	        		return new PasswordAuthentication("dipak1657445@gmail.com","atna tlzu mlui dxiu");
	        	}
			});

	        session.setDebug(true);

	        //step 2:Compose the message
	        MimeMessage m = new MimeMessage(session);

	        try{
	            //from email
	            m.setFrom(from);

	            //adding recipient to message
	            m.addRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress(to)});

	            //adding subject to message
	            m.setSubject(subject);

	            //adding text to message
//	            m.setText(message);
	            m.setContent(message,"text/html");
	            //send
	            //step 3: send the message using Transport class
	            Transport.send(m);

	            System.out.println("Sent success......");

	            f=true;

	        }catch (Exception e){
	            e.printStackTrace();
	        }
	        return f;
	    }
	
}
