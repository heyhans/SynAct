package ai.synact.synact.services;

import ai.synact.synact.dto.ContactMessageRequest;
import ai.synact.synact.entities.ContactMessage;
import ai.synact.synact.repositories.ContactMessageRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;

    public Long save(ContactMessageRequest req, HttpServletRequest request) {
        String ip = getClientIp(request);
        String ua = request.getHeader("User-Agent");

        ContactMessage entity = ContactMessage.builder()
                .firstName(req.getFirstName().trim())
                .lastName(req.getLastName().trim())
                .email(req.getEmail().trim())
                .phone(req.getPhone().trim())
                .company(req.getCompany() == null ? null : req.getCompany().trim())
                .message(req.getMessage().trim())
                .ipAddress(ip)
                .userAgent(ua != null && ua.length() > 255 ? ua.substring(0, 255) : ua)
                .telegramSent(false)
                .build();

        return contactMessageRepository.save(entity).getId();
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For"); // if behind proxy
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        String xri = request.getHeader("X-Real-IP");
        if (xri != null && !xri.isBlank()) return xri.trim();

        return request.getRemoteAddr();
    }
}
