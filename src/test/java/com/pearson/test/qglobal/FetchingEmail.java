package com.pearson.test.qglobal;

import com.sun.mail.util.BASE64DecoderStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Message.RecipientType;

public class FetchingEmail {
    public FetchingEmail() {
    }

    public static void fetch(String pop3Host, String storeType, String user, String password) {
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            Message[] messages = emailFolder.getMessages();
            System.out.println("messages.length---" + messages.length);

            for(int i = 0; i < messages.length; ++i) {
                Message message = messages[i];
                System.out.println("---------------------------------");
                writePart(message);
                String line = reader.readLine();
                if ("YES".equals(line)) {
                    message.writeTo(System.out);
                } else if ("QUIT".equals(line)) {
                    break;
                }
            }

            emailFolder.close(false);
            store.close();
        } catch (NoSuchProviderException var13) {
            var13.printStackTrace();
        } catch (MessagingException var14) {
            var14.printStackTrace();
        } catch (IOException var15) {
            var15.printStackTrace();
        } catch (Exception var16) {
            var16.printStackTrace();
        }

    }

    public static void main(String[] args) {
        String host = "pop.gmail.com";
        String mailStoreType = "pop3";
        String username = "pearsonqglobal@gmail.com";
        String password = "Pearson@1";
        fetch(host, mailStoreType, username, password);
    }

    public static void writePart(Part p) throws Exception {
        if (p instanceof Message) {
            writeEnvelope((Message)p);
        }

        System.out.println("----------------------------");
        System.out.println("CONTENT-TYPE: " + p.getContentType());
        if (p.isMimeType("text/plain")) {
            System.out.println("This is plain text");
            System.out.println("---------------------------");
            System.out.println((String)p.getContent());
        } else {
            int c;
            if (p.isMimeType("multipart/*")) {
                System.out.println("This is a Multipart");
                System.out.println("---------------------------");
                Multipart mp = (Multipart)p.getContent();
                int count = mp.getCount();

                for(c = 0; c < count; ++c) {
                    writePart(mp.getBodyPart(c));
                }
            } else if (p.isMimeType("message/rfc822")) {
                System.out.println("This is a Nested Message");
                System.out.println("---------------------------");
                writePart((Part)p.getContent());
            } else if (p.getContentType().contains("image/")) {
                System.out.println("content type" + p.getContentType());
                File f = new File("image" + (new Date()).getTime() + ".jpg");
                DataOutputStream output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
                BASE64DecoderStream test = (BASE64DecoderStream)p.getContent();
                byte[] buffer = new byte[1024];

                int bytesRead;
                while((bytesRead = test.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } else {
                Object o = p.getContent();
                if (o instanceof String) {
                    System.out.println("This is a string");
                    System.out.println("---------------------------");
                    System.out.println((String)o);
                } else if (o instanceof InputStream) {
                    System.out.println("This is just an input stream");
                    System.out.println("---------------------------");
                    InputStream is = (InputStream)o;
                    is = (InputStream)o;

                    while((c = is.read()) != -1) {
                        System.out.write(c);
                    }
                } else {
                    System.out.println("This is an unknown type");
                    System.out.println("---------------------------");
                    System.out.println(o.toString());
                }
            }
        }

    }

    public static void writeEnvelope(Message m) throws Exception {
        System.out.println("This is the message envelope");
        System.out.println("---------------------------");
        Address[] a;
        int j;
        if ((a = m.getFrom()) != null) {
            for(j = 0; j < a.length; ++j) {
                System.out.println("FROM: " + a[j].toString());
            }
        }

        if ((a = m.getRecipients(RecipientType.TO)) != null) {
            for(j = 0; j < a.length; ++j) {
                System.out.println("TO: " + a[j].toString());
            }
        }

        if (m.getSubject() != null) {
            System.out.println("SUBJECT: " + m.getSubject());
        }

    }
}
