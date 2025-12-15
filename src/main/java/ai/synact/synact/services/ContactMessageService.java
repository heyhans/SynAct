package ai.synact.synact.services;

import ai.synact.synact.dto.ContactMessageRequest;
import ai.synact.synact.entities.ContactMessage;
import ai.synact.synact.repositories.ContactMessageRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final TelegramService telegramService;

    @Transactional
    public void save(ContactMessageRequest request, HttpServletRequest httpRequest) {

        ContactMessage saved = contactMessageRepository.save(
                ContactMessage.builder()
                        .firstName(request.getFirstName())
                        .lastName(request.getLastName())
                        .email(request.getEmail())
                        .phone(request.getPhone())
                        .company(request.getCompany())
                        .message(request.getMessage())
                        .ipAddress(getClientIp(httpRequest))
                        .userAgent(safeTruncate(httpRequest.getHeader("User-Agent"), 255))
                        .telegramSent(false)
                        .build()
        );

        String telegramText = buildTelegramText(saved);

        boolean ok = telegramService.sendContactMessage(telegramText);
        if (ok) {
            saved.setTelegramSent(true);
            saved.setTelegramSentAt(LocalDateTime.now());
            contactMessageRepository.save(saved);
        }
    }

    private String buildTelegramText(ContactMessage m) {
        // Telegram HTML mode: escape <, >, &
        return """
                <b>📩 New Contact Message</b>
                <b>Name:</b> %s %s
                <b>Email:</b> %s
                <b>Phone:</b> %s
                <b>Company:</b> %s
                <b>Message:</b> %s
                <b>IP:</b> %s
                """.formatted(
                esc(m.getFirstName()),
                esc(m.getLastName()),
                esc(m.getEmail()),
                esc(m.getPhone()),
                esc(m.getCompany() == null ? "-" : m.getCompany()),
                esc(m.getMessage()),
                esc(m.getIpAddress() == null ? "-" : m.getIpAddress())
        );
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String safeTruncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }

    private String getClientIp(HttpServletRequest req) {
        String xf = req.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) return xf.split(",")[0].trim();
        return req.getRemoteAddr();
    }
}