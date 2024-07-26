package campaignms.campaignms.services;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// import javax.mail.Message;
// import javax.mail.MessagingException;
// import javax.mail.PasswordAuthentication;
// import javax.mail.Session;
// import javax.mail.Transport;
// import javax.mail.internet.InternetAddress;
// import javax.mail.internet.MimeMessage;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service

public class JakartaEmailService {
   private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

   @Value("${mail.reply-to}")
   private String replyTo;

   @Value("${mail.from}")
    private String from;

   public void sendEmail(String to, String subject, String body) {
      //provide Mailtrap's username
      final String username = "dedeherdiana94@gmail.com";
      //provide Mailtrap's password
      final String password = "aoaf pmej msvq hnep";
      //provide Mailtrap's host address
      String host = "smtp.gmail.com";
      //configure Mailtrap's SMTP server details
      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", host);
      props.put("mail.smtp.port", "587");
      //create the Session object
      Authenticator authenticator = new Authenticator() {
         protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
         }
      };
      Session session = Session.getInstance(props, authenticator);
      try {
         //create a MimeMessage object
         Message message = new MimeMessage(session);
         //set From email field
         message.setFrom(new InternetAddress(from));
         //set To email field
         message.setRecipients(Message.RecipientType.TO,
                     InternetAddress.parse(to));
         //set email subject field
         message.setSubject(subject);
         //set the content of the email message
         message.setContent(body, "text/html; charset=UTF-8");
         //send the email message
         Transport.send(message);
         logger.info("Email sent successfully to: {}", to);

      } catch (MessagingException e) {
         logger.error("Failed to send email to: {}", to, e);

         throw new RuntimeException(e);
      }
   }
   // Pastikan metode ini public dan menerima parameter yang tepat
   public void onMessage(String message) {
      logger.info("Received message: {}", message);
      try {
          // Remove leading and trailing quotes from the message string
          message = message.replaceAll("^\"|\"$", "");
          String[] parts = message.split("\\|");
          if (parts.length == 3) {
              String email = parts[0].trim();
              String subject = parts[1].trim();
              String body = parts[2].trim();
              
              logger.info("emails: {}", email);
              logger.info("Subject: {}", subject);
              logger.info("Body: {}", body);
              sendEmail(email, subject, body);
          } else {
              logger.error("Invalid message format: {}", message);
          }
      } catch (Exception e) {
          logger.error("Failed to process message: {}", message, e);
      }
  }
}