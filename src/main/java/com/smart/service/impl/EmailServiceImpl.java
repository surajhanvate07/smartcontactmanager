package com.smart.service.impl;

import com.smart.service.EmailService;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {
    @Override
    public boolean sendEmail(String subject, String message, String to) {
        boolean check = false;
        String from = "hanvatesuraj9@gmail.com";

        //variable for gmail
        String host = "smtp.gmail.com";

        //get the system properties
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Step 1 : Get Session Object
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("hanvatesuraj9@gmail.com", "rrxzwqgolbazedhm");
            }
        });
        session.setDebug(true);

        // Step 2 : Compose the message
        MimeMessage msg = new MimeMessage(session);

        try {
            msg.setFrom(from);

            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            msg.setSubject(subject);

            msg.setText(message);

            Transport.send(msg);

            System.out.println("Sent Success.....");
            check = true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return check;
    }
}
