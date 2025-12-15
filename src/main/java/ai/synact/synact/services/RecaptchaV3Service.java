package ai.synact.synact.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class RecaptchaV3Service {

    @Value("${google.recaptcha.secret-key}")
    private String secretKey;

    @Value("${google.recaptcha.min-score:0.5}")
    private double minScore;

    private final WebClient webClient = WebClient.builder().build();

    public VerificationResult verify(String token, String expectedAction, HttpServletRequest request) {
        if (token == null || token.isBlank()) {
            return VerificationResult.fail("Missing reCAPTCHA token.");
        }

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("secret", secretKey);
        formData.add("response", token);
        formData.add("remoteip", getClientIp(request)); // optional but good

        RecaptchaResponse resp = webClient.post()
                .uri("https://www.google.com/recaptcha/api/siteverify")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(RecaptchaResponse.class)
                .block();

        if (resp == null || !resp.isSuccess()) {
            return VerificationResult.fail("reCAPTCHA verification failed.");
        }

        // v3 checks
        if (resp.getAction() == null || !resp.getAction().equals(expectedAction)) {
            return VerificationResult.fail("reCAPTCHA action mismatch.");
        }

        if (resp.getScore() < minScore) {
            return VerificationResult.fail("reCAPTCHA score too low.");
        }

        return VerificationResult.ok(resp.getScore());
    }

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        String xri = request.getHeader("X-Real-IP");
        if (xri != null && !xri.isBlank()) return xri.trim();
        return request.getRemoteAddr();
    }

    @Data
    public static class RecaptchaResponse {
        private boolean success;
        private double score;
        private String action;
        // other fields exist but are optional: challenge_ts, hostname, error-codes
    }

    @Data
    public static class VerificationResult {
        private boolean ok;
        private String message;
        private Double score;

        public static VerificationResult ok(double score) {
            VerificationResult r = new VerificationResult();
            r.ok = true; r.score = score;
            return r;
        }

        public static VerificationResult fail(String msg) {
            VerificationResult r = new VerificationResult();
            r.ok = false; r.message = msg;
            return r;
        }
    }
}