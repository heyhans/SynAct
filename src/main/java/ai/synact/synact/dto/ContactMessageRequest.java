package ai.synact.synact.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactMessageRequest {

    // English letters only (A–Z), allows space, hyphen, apostrophe
    @NotBlank(message = "First name is required.")
    @Pattern(
            regexp = "^[A-Za-z]+(?:[ '-][A-Za-z]+)*$",
            message = "First name must contain English letters only."
    )
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Pattern(
            regexp = "^[A-Za-z]+(?:[ '-][A-Za-z]+)*$",
            message = "Last name must contain English letters only."
    )
    private String lastName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Please enter a valid email address.")
    private String email;

    // E.164 format: +821012345678
    @NotBlank(message = "Phone number is required.")
    @Pattern(
            regexp = "^\\+[1-9]\\d{7,14}$",
            message = "Phone must include country code (e.g. +821012345678)."
    )
    private String phone;

    @Size(max = 255, message = "Company name must be 255 characters or less.")
    private String company;

    @NotBlank(message = "Message is required.")
    @Size(max = 2000, message = "Message must be 2000 characters or less.")
    private String message;
}
