package ai.synact.synact.repositories;

import ai.synact.synact.entities.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    // Find all messages ordered by newest first
    List<ContactMessage> findAllByOrderByCreatedAtDesc();

    // Find messages by email (useful for admin / duplicate checks)
    List<ContactMessage> findByEmail(String email);

    // Find messages that failed Telegram delivery (for retry jobs)
    List<ContactMessage> findByTelegramSentFalse();
}
