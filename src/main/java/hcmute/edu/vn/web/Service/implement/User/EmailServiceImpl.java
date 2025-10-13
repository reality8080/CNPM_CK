package hcmute.edu.vn.web.Service.implement.User;

import hcmute.edu.vn.web.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail); // Nếu bạn cần set rõ from email
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            // Log lỗi, ví dụ: "Không thể gửi email đến " + to
            System.err.println("Error sending email to " + to + ": " + e.getMessage());
            // Tùy chọn: ném một exception tùy chỉnh để Controller xử lý
            throw new RuntimeException("Không thể gửi email kích hoạt.", e);
        }
    }

}