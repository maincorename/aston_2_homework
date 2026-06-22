import application.NotificationServiceApplication;
import application.services.EmailService;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = NotificationServiceApplication.class)
class NotificationServiceIntegrationTest {

    private GreenMail greenMail;

    @Autowired
    private EmailService emailService;

    @DynamicPropertySource
    static void configureMail(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", () -> "3025");
        registry.add("app.mail.from", () -> "test@example.com");
    }

    @BeforeEach
    void setUp() {
        greenMail = new GreenMail(new ServerSetup(3025, null, "smtp"));
        greenMail.start();
    }

    @AfterEach
    void tearDown() {
        greenMail.stop();
    }

    @Test
    void shouldSendWelcomeEmail() {
        emailService.sendWelcomeEmail("user@example.com");
        assertEquals(1, greenMail.getReceivedMessages().length);
    }

    @Test
    void shouldSendDeletionEmail() {
        emailService.sendDeletionEmail("user@example.com");
        assertEquals(1, greenMail.getReceivedMessages().length);
    }
}