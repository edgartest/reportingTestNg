package com.pearson.test.qglobal;

import com.pearson.common.Email;
import java.io.IOException;
import javax.mail.MessagingException;

public class TestEmail {
    public TestEmail() {
    }

    public static void main(String[] args) throws MessagingException, IOException {
        Email email = new Email();
        String link = email.getLink("Welcome to Q-global");
        System.out.println(link);
    }
}
