package ai.synact.synact.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class TelegramService {

    private final WebClient.Builder webClientBuilder;

    @Value("${telegram.bot-token}")
    private String botToken;

    @Value("${telegram.chat-id}")
    private String chatId;

    public boolean sendContactMessage(String text) {
        String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("chat_id", chatId);
        form.add("text", text);
        form.add("parse_mode", "HTML"); // optional (you can remove if you want plain text)
        form.add("disable_web_page_preview", "true");

        try {
            TelegramResponse res = webClientBuilder.build()
                    .post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(form)
                    .retrieve()
                    .bodyToMono(TelegramResponse.class)
                    .block();

            return res != null && res.isOk();
        } catch (Exception e) {
            return false;
        }
    }

    // minimal DTO for Telegram API response
    @lombok.Data
    public static class TelegramResponse {
        private boolean ok;
    }
}
