package ai.synact.synact.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;

    @Column(name = "email", length = 255, nullable = false)
    private String email;

    // E.164 format: +8210..., keep it as String
    @Column(name = "phone", length = 20, nullable = false)
    private String phone;

    @Column(name = "company", length = 255)
    private String company;

    // You will enforce 2000 chars (or whatever) in validation later
    @Lob
    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "telegram_sent", nullable = false)
    private Boolean telegramSent = false;

    @Column(name = "telegram_sent_at")
    private LocalDateTime telegramSentAt;

    // DB manages these automatically (DEFAULT CURRENT_TIMESTAMP / ON UPDATE)
    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
