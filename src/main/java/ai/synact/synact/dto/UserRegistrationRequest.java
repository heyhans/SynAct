package ai.synact.synact.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO, not persisted
public record UserRegistrationRequest(
        @NotBlank(message = "{val.name.required}")
        @Size(max = 100, message = "{val.name.size}")
        String name,

        @NotBlank(message = "{val.email.required}")
        @Email(message = "{val.email.format}")
        @Size(max = 191, message = "{val.email.size}")
        String email,

        @NotBlank(message = "{val.password.required}")
        @Size(min = 8, max = 100, message = "{val.password.size}")
        String password
) {}