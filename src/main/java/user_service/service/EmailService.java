package user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * EmailService - sends emails for forgot password
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Send password reset email with token link
     */
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject("🏨 HotelBook - Password Reset Request");
            message.setText(
                    "Hello,\n\n" +
                            "You requested to reset your password.\n\n" +
                            "Click the link below to reset your password:\n" +
                            "http://localhost:5173/reset-password?token=" + token + "\n\n" +
                            "This link expires in 15 minutes.\n\n" +
                            "If you didn't request this, ignore this email.\n\n" +
                            "Thanks,\nHotelBook Team"
            );
            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send email");
        }
    }
}