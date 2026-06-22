package application.kafka;

import application.dto.UserEventDto;
import application.services.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationKafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationKafkaConsumer.class);

    private final EmailService emailService;

    public NotificationKafkaConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "${app.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(UserEventDto event) {
        logger.info("Received event: {}", event);

        switch (event.operation()) {
            case "CREATED":
                emailService.sendWelcomeEmail(event.email());
                break;
            case "DELETED":
                emailService.sendDeletionEmail(event.email());
                break;
            default:
                logger.warn("Unknown operation: {}", event.operation());
        }
    }
}
