package ai.synact.synact.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = false, nullable = false)
    private String name;

    // NOTE: this must match the service’s getPasswordHash() call
    @Column(name = "password_hash")
    private String passwordHash;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    // com.vinohub.entities.User
    // (only new fields shown)
    @Column(name = "email_verify_token", length = 64, unique = true)
    private String emailVerifyToken;        // null once verified

    @Column(name = "email_verify_expires_at")
    private LocalDateTime emailVerifyExpiresAt;

    @Column(name = "email_verified_at")
    private LocalDateTime emailVerifiedAt;  // auditing/analytics

    @Column(name = "email_verify_last_sent_at")
    private LocalDateTime emailVerifyLastSentAt;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<SocialAccount> socialAccounts = new HashSet<>();
}