package campaignms.campaignms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import campaignms.campaignms.models.EmailMassLog;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailMassLogService emailMassLogService;
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${mail.reply-to}")
    private String replyTo;

    @Value("${mail.from}")
    private String from;

    public void sendEmail(String to, String subject, String body) {
        EmailMassLog log = new EmailMassLog();
        log.setEmail(to);
        log.setSubject(subject);
        log.setContent(body);
        emailMassLogService.save(log);
    
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        

        // Decode the HTML body
        String decodedBody;
        try {
            // decodedBody = URLDecoder.decode(body, StandardCharsets.UTF_8.name());
            decodedBody = java.net.URLDecoder.decode(body, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            // Handle error or set a default value
            decodedBody = body; // Default to the original body if decoding fails
        }
        try {
            helper = new MimeMessageHelper(message, true,"UTF-8"); // second parameter indicates multipart message
            helper.setFrom(from); // use the from value from properties file
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(decodedBody, true); // second parameter indicates HTML format
            helper.setReplyTo(replyTo);
            javaMailSender.send(message);

    
            logger.info("Email sent successfully to: {}", to);
            log.setStatus("SUCCESS");
        } catch (Exception e) {
            logger.error("Failed to send email to: {}", to, e);
            log.setStatus("FAILED");
        }
    
        emailMassLogService.save(log);
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
