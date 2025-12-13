package ai.synact.synact.services;

import ai.synact.synact.dto.UserRegistrationRequest;
import ai.synact.synact.entities.Role;
import ai.synact.synact.entities.User;
import ai.synact.synact.repositories.RoleRepository;
import ai.synact.synact.repositories.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final int TOKEN_TTL_MIN = 60; // 1h
    private static final int RESEND_THROTTLE_SEC = 60; // 1 minute throttle

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService; // your SMTP service

    @Transactional
    public User registerNewUser(UserRegistrationRequest req, String roleName, String baseUrl) throws MessagingException {
        // 1) Basic checks
        if (userRepo.existsByEmail(req.email().trim().toLowerCase())) {
            throw new IllegalArgumentException("Email already in use");
        }

        Role role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role not found: " + roleName));

        // 2) Create user disabled
        String token = generateToken();
        LocalDateTime expires = LocalDateTime.now().plusMinutes(TOKEN_TTL_MIN);

        User user = User.builder()
                .email(req.email().trim().toLowerCase())
                .name(req.name().trim())
                .passwordHash(passwordEncoder.encode(req.password()))
                .enabled(false)
                .emailVerifyToken(token)
                .emailVerifyExpiresAt(expires)
                .build();

        user.getRoles().add(role);
        user = userRepo.save(user);

        // 3) Send verification email
        String verifyUrl = baseUrl + "/auth/verify?token=" + token;
        String html = """
            <h3>Verify your email</h3>
            <p>Hi %s,</p>
            <p>Please confirm your email to activate your account.</p>
            <p><a href="%s">Verify email</a></p>
            <p>This link expires in %d minutes.</p>
            """.formatted(user.getName(), verifyUrl, TOKEN_TTL_MIN);

        mailService.sendHtml(user.getEmail(), "Verify your VinoHub account", html);

        return user;
    }

    @Transactional
    public boolean verifyByToken(String token) {
        var userOpt = userRepo.findByEmailVerifyToken(token);
        if (userOpt.isEmpty()) return false;

        User u = userOpt.get();
        if (u.getEmailVerifyExpiresAt() == null || u.getEmailVerifyExpiresAt().isBefore(LocalDateTime.now())) {
            return false; // expired
        }

        u.setEnabled(true);
        u.setEmailVerifiedAt(LocalDateTime.now());
        u.setEmailVerifyToken("Verified");
        userRepo.save(u);
        return true;
    }

    @Transactional
    public void resendVerification(Long userId, String baseUrl) throws MessagingException {
        User u = userRepo.findById(userId).orElseThrow();
        if (Boolean.TRUE.equals(u.isEnabled())) return; // already verified

        // rotate token
        u.setEmailVerifyToken(generateToken());
        u.setEmailVerifyExpiresAt(LocalDateTime.now().plusMinutes(TOKEN_TTL_MIN));
        userRepo.save(u);

        String verifyUrl = baseUrl + "/auth/verify?token=" + u.getEmailVerifyToken();
        String html = """
            <h3>Verify your email</h3>
            <p>Hi %s,</p>
            <p>Here is your new verification link:</p>
            <p><a href="%s">Verify email</a></p>
            """.formatted(u.getName(), verifyUrl);

        mailService.sendHtml(u.getEmail(), "Your verification link", html);
    }

    private static String generateToken() {
        byte[] b = new byte[24];
        new java.security.SecureRandom().nextBytes(b);
        return java.util.HexFormat.of().formatHex(b);
    }

    @Transactional
    public void resendVerificationByEmail(String email, String baseUrl) throws MessagingException {
        User u = userRepo.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("No account with that email"));

        if (Boolean.TRUE.equals(u.isEnabled())) {
            // Already verified—nothing to resend.
            return;
        }

        // Throttle: allow once per minute
        if (u.getEmailVerifyLastSentAt() != null &&
                u.getEmailVerifyLastSentAt().isAfter(LocalDateTime.now().minusSeconds(RESEND_THROTTLE_SEC))) {
            throw new IllegalStateException("Please wait before requesting another email.");
        }

        // Rotate token & expiry
        u.setEmailVerifyToken(generateToken());
        u.setEmailVerifyExpiresAt(LocalDateTime.now().plusMinutes(TOKEN_TTL_MIN));
        u.setEmailVerifyLastSentAt(LocalDateTime.now());
        userRepo.save(u);

        // Send mail
        String verifyUrl = baseUrl + "/auth/verify?token=" + u.getEmailVerifyToken();
        String html = """
            <h3>Verify your email</h3>
            <p>Hi %s,</p>
            <p>Here is your verification link:</p>
            <p><a href="%s">Verify email</a></p>
            <p>This link expires in %d minutes.</p>
            """.formatted(u.getName(), verifyUrl, TOKEN_TTL_MIN);

        mailService.sendHtml(u.getEmail(), "Your verification link", html);
    }
}
