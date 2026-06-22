package application.controllers;

import application.services.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/welcome")
    public ResponseEntity<String> sendWelcomeEmail(@RequestParam String email) {
        emailService.sendWelcomeEmail(email);
        return ResponseEntity.ok("Welcome email sent to " + email);
    }

    @PostMapping("/deletion")
    public ResponseEntity<String> sendDeletionEmail(@RequestParam String email) {
        emailService.sendDeletionEmail(email);
        return ResponseEntity.ok("Deletion email sent to " + email);
    }
}