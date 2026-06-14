package application.kafka;

import application.dto.UserEventDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventProducer.class);

    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    public UserEventProducer(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> props = Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
        );
        this.kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    }

    public void sendUserCreated(String email) {
        UserEventDto event = new UserEventDto("CREATED", email);
        kafkaTemplate.send(topic, event);
        logger.info("Sent CREATED event for email: {}", email);
    }

    public void sendUserDeleted(String email) {
        UserEventDto event = new UserEventDto("DELETED", email);
        kafkaTemplate.send(topic, event);
        logger.info("Sent DELETED event for email: {}", email);
    }
}