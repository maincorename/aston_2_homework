package application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
        logger.info("Email sent to: {} with subject: {}", to, subject);
    }

    public void sendWelcomeEmail(String to) {
        sendEmail(to, "Добро пожаловать!",
                "Здравствуйте! Ваш аккаунт на сайте был успешно создан.");
    }

    public void sendDeletionEmail(String to) {
        sendEmail(to, "Аккаунт удалён",
                "Здравствуйте! Ваш аккаунт был удалён.");
    }
}
