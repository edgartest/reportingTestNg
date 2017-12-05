package com.pearson.test.qglobal;

import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class SelectEmail {
    public SelectEmail() {
    }

    public void fetch(String pop3Host, String storeType, String user, String password) {
        try {
            Properties properties = new Properties();
            properties.put("mail.store.protocol", "pop3");
            properties.put("mail.pop3.host", pop3Host);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);
            Store store = emailSession.getStore("pop3s");
            store.connect(pop3Host, user, password);
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(1);
            Message[] messages = emailFolder.getMessages();
            System.out.println("messages.length---" + messages.length);
            Message msg = emailFolder.getMessage(emailFolder.getMessageCount());
            System.out.println(msg.getSubject());
            Long currentTime = Calendar.getInstance().getTime().getTime();
            System.out.println("Current Time milliseconds " + currentTime);
            Long sentTime = msg.getSentDate().getTime();
            System.out.println("Sent Time milliseconds " + sentTime);
            String contentEmail = "";
            int count;
            if (currentTime.longValue() - sentTime.longValue() < 300000L) {
                System.out.println("Email not sent");
            } else if (msg.getSubject().contains("Welcome to Q-global") && msg.isMimeType("multipart/*")) {
                System.out.println("This is a Multipart");
                System.out.println("---------------------------");
                Multipart mp = (Multipart)msg.getContent();
                count = mp.getCount();

                for(int i = 0; i < count; ++i) {
                    Object o = mp.getBodyPart(i).getContent();
                    if (o instanceof String) {
                        contentEmail = (String)o;
                        break;
                    }
                }
            }

            System.out.println(contentEmail);
            int clickHereStart = contentEmail.indexOf("Click here") - 2;
            count = contentEmail.lastIndexOf("a href=", clickHereStart) + 8;
            contentEmail.substring(count, clickHereStart);
            emailFolder.close(false);
            store.close();
        } catch (NoSuchProviderException var19) {
            var19.printStackTrace();
        } catch (MessagingException var20) {
            var20.printStackTrace();
        } catch (IOException var21) {
            var21.printStackTrace();
        } catch (Exception var22) {
            var22.printStackTrace();
        }

    }

    public static void main(String[] args) {
        String host = "pop.gmail.com";
        String mailStoreType = "pop3";
        String username = "pearsonqglobal@gmail.com";
        String password = "Pearson@1";
        SelectEmail select = new SelectEmail();
        select.fetch(host, mailStoreType, username, password);
    }
}