package ai.synact.synact.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:VinoHub <noreply@yourdomain.com>}")
    private String defaultFrom;

    /** Simple plaintext */
    public void sendText(String to, String subject, String body) {
        var msg = new SimpleMailMessage();
        msg.setFrom(defaultFrom);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
    }

    /** HTML (inline) */
    public void sendHtml(String to, String subject, String html) throws MessagingException {
        MimeMessage mime = mailSender.createMimeMessage();
        // multipart=false is fine for pure HTML
        MimeMessageHelper helper = new MimeMessageHelper(mime, false, StandardCharsets.UTF_8.name());
        helper.setFrom(defaultFrom);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true); // true => HTML
        mailSender.send(mime);
    }

    /** HTML with single attachment (example) */
    public void sendHtmlWithAttachment(String to, String subject, String html,
                                       byte[] fileBytes, String filename, String contentType)
            throws MessagingException {
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, true, StandardCharsets.UTF_8.name());
        helper.setFrom(defaultFrom);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        InputStreamSource src = new ByteArrayResource(fileBytes);
        helper.addAttachment(filename, src, contentType);

        mailSender.send(mime);
    }

    /** Tiny convenience for variable substitution (if you don't want Thymeleaf) */
    public void sendHtmlTemplate(String to, String subject, String template, Map<String, String> vars)
            throws MessagingException {
        String html = template;
        if (vars != null) {
            for (var e : vars.entrySet()) {
                html = html.replace("{{" + e.getKey() + "}}", e.getValue());
            }
        }
        sendHtml(to, subject, html);
    }
}
