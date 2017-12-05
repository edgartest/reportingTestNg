package com.pearson.test.qglobal;

import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class CheckingMails {
    public CheckingMails() {
    }

    public static void check(String host, String storeType, String user, String password) {
        try {
            Properties properties = new Properties();
            properties.put("mail.pop3.host", host);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore("pop3s");
            store.connect(host, user, password);
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(1);
            Message[] messages = emailFolder.getMessages();
            System.out.println("messages.length---" + messages.length);
            int i = 0;

            for(int n = messages.length; i < n; ++i) {
                Message message = messages[i];
                System.out.println("---------------------------------");
                System.out.println("Email Number " + (i + 1));
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Text: " + message.getContent().toString());
            }

            emailFolder.close(false);
            store.close();
        } catch (NoSuchProviderException var12) {
            var12.printStackTrace();
        } catch (MessagingException var13) {
            var13.printStackTrace();
        } catch (Exception var14) {
            var14.printStackTrace();
        }

    }

    public static void main(String[] args) {
        String host = "pop.gmail.com";
        String mailStoreType = "pop3";
        String username = "pearsonqglobal@gmail.com";
        String password = "Pearson@1";
        check(host, mailStoreType, username, password);
    }
}