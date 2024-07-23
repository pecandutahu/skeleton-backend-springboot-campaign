package campaignms.campaignms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import campaignms.campaignms.models.EmailMassLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import campaignms.campaignms.models.Customer;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private EmailMassLogService emailMassLogService;
    
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(String to, String subject, String body) {
        

        EmailMassLog log = new EmailMassLog();
        log.setEmail(to);
        log.setSubject(subject);
        log.setContent(body);
        emailMassLogService.save(log);

        SimpleMailMessage message = new SimpleMailMessage();
        try {
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
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
