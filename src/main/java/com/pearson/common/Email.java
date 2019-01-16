package com.pearson.common;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

public class Email {
	
	private Store store;
	private Folder emailFolder;
	private Message msg;

	private String username = "";
	private String password = "";
	private String mainContent = "";

	public Email(){
		setCredentials("test", "test");
	}

	private void setCredentials(String username, String password){
		this.username = username;
		this.password = password;
	}
	
	private void setConnectionPOP() throws MessagingException{
		
		String mailStoreType = "pop3";
		String host = "pop.gmail.com";
		String port = "995";

        Properties properties = new Properties();
        properties.put("mail.store.protocol", mailStoreType);
        properties.put("mail.pop3.host", host);
        properties.put("mail.pop3.port", port);
        properties.put("mail.pop3.starttls.enable", "true");
        Session emailSession = Session.getDefaultInstance(properties);

        this.store = emailSession.getStore("pop3s");
        this.store.connect(host, username, password);
	}

	private void setConnectionIMAP() throws MessagingException{

		String mailStoreType = "imaps";
		String host = "imap.gmail.com";
		String port = "993";

		Properties properties = new Properties();
		properties.put("mail.store.protocol", mailStoreType);
		/*properties.put("mail.pop3.host", host);
		properties.put("mail.pop3.port", port);
		properties.put("mail.pop3.starttls.enable", "true");*/
		Session emailSession = Session.getDefaultInstance(properties);

		this.store = emailSession.getStore("imaps");
		this.store.connect(host, username, password);
	}

	private void retriveLatestMail() throws MessagingException{
        this.emailFolder = store.getFolder("INBOX");
		this.emailFolder.open(Folder.READ_ONLY);
        this.msg = emailFolder.getMessage(emailFolder.getMessageCount());
	}

	private void getMessage(String subject) throws MessagingException{
		this.emailFolder = store.getFolder("INBOX");
		this.emailFolder.open(Folder.READ_ONLY);
		Message[] messages = emailFolder.getMessages();

		for(int i=0; i< messages.length; i++){
			this.msg = emailFolder.getMessage(i+1);
			if(validateMsg(subject, 300000))
				break;
		}
		//if no email found will return the latest one

	}
	
	private boolean validateMsg(String subject, long maxTime) throws MessagingException{
		Long currentTime = Calendar.getInstance().getTime().getTime();
        Long sentTime = msg.getSentDate().getTime();
        // Real should be <  ; for testing purpose we used >
        if((currentTime - sentTime < maxTime) && msg.getSubject().contains(subject)){
       	 	return true;
        }
        else
        	return false;
	}

	public void writePart(Part p) throws Exception {
		//check if the content is plain text
		if (p.isMimeType("text/plain")) {
			mainContent = (String) p.getContent();
		}
		//check if the content has attachment
		else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart) p.getContent();
			int count = mp.getCount();
			for (int i = 0; i < count; i++)
				writePart(mp.getBodyPart(i));
		}
		//check if the content is a nested message
		else if (p.isMimeType("message/rfc822")) {
			writePart((Part) p.getContent());
		}
		//check if the content is an inline image
		else if (p.isMimeType("image/jpeg")) {
			Object o = p.getContent();
			InputStream x = (InputStream) o;

		}
		else if (p.getContentType().contains("image/")) {
			File f = new File("image" + new Date().getTime() + ".jpg");
			DataOutputStream output = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(f)));
			com.sun.mail.util.BASE64DecoderStream test =
					(com.sun.mail.util.BASE64DecoderStream) p
							.getContent();
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = test.read(buffer)) != -1) {
				output.write(buffer, 0, bytesRead);
			}
		}
		else {
			Object o = p.getContent();
			if (o instanceof String) {
				mainContent = (String) o;
			}
			else if (o instanceof InputStream) {
				InputStream is = (InputStream) o;
				is = (InputStream) o;
				int c;
				while ((c = is.read()) != -1)
					System.out.write(c);
			}
			else {
				mainContent = o.toString();
			}
		}

	}

	private String retriveLink() throws MessagingException, IOException{
	 String contentEmail = "";
   	 if (msg.isMimeType("multipart/*")) {
         Multipart mp = (Multipart) msg.getContent();
         int count = mp.getCount();
         for (int i = 0; i < count; i++){                 
            Object o = mp.getBodyPart(i).getContent();
            if (o instanceof String) {
            	contentEmail = (String) o;
            	break;
            }
			else if (o instanceof InputStream) {
				InputStream is = (InputStream) o;
				is = (InputStream) o;
				int c;
				while ((c = is.read()) != -1)
					System.out.write(c);
			}
			else {
				System.out.println(o.toString());
			}
         }
      }

     int clickHereStart = contentEmail.indexOf("Click here")-2;
     int clickHereLinkStart = contentEmail.lastIndexOf("a href=", clickHereStart)+8;
     
     return contentEmail.substring(clickHereLinkStart, clickHereStart);		
	}
	
	private void closeConnection() throws MessagingException{
        emailFolder.close(false);
        store.close();
	}
	
	public String getLink(String subjectEmail) throws MessagingException, IOException{
		String link = "";
		setConnectionPOP();
		retriveLatestMail();
		//5 min max difference
		if(!validateMsg(subjectEmail, 300000)){
			link = "Email not found";
		}
		link = retriveLink();
		closeConnection();
		return link;
	}

	public String getContent(String subjectEmail) throws Exception{
		setConnectionPOP();
		//setConnectionIMAP();
		//retriveLatestMail();
		getMessage(subjectEmail);
		//5 min max difference
		/*if(!validateMsg(subjectEmail, 300000)){
			writePart(msg);
			closeConnection();
		}*/
		writePart(msg);
		closeConnection();
		return mainContent;
	}
}
