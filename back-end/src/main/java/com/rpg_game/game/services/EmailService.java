package com.rpg_game.game.services;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmailAddress;

    @Value("${sendgrid.from.name}")
    private String fromName;

    /**
     * Sends email to the SendGrid API
     * 
     * @param toRecipientEmail The email address of the recipient
     * @param subject The subject line of the email.
     * @param body The text content of the email.
     */
    public void sendEmail(String toRecipientEmail, String subject, String body) {
        Email from = new Email(fromEmailAddress, fromName);
        Email to = new Email(toRecipientEmail);
        Content content = new Content("text/html", body);

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            logger.info("Attempting to send email to {}", toRecipientEmail);
            Response response = sg.api(request);

            logger.info("SendGrid Response Status Code: {}", response.getStatusCode());
            logger.info("SendGrid Response Body: {}", response.getBody());
            logger.info("SendGrid Response Headers: {}", response.getHeaders());
            
            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                logger.info("Email sent successfully to {}", toRecipientEmail);
            } else {
                logger.error("Failed to send email to {}. Status: {}, Body: {}",
                        toRecipientEmail, response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to send email via SendGrid. Status: " + response.getStatusCode());
            }
        } catch (IOException e) {
            logger.error("Error sending email to {}: {}", toRecipientEmail, e.getMessage());
            throw new RuntimeException("Error sending email via SendGrid", e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred while sending email to {}: {}", toRecipientEmail, e.getMessage());
            throw new RuntimeException("An unexpected error occurred during email sending", e);
        }
    }
}
