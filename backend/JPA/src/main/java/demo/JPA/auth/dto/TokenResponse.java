package demo.JPA.auth.dto;

public record TokenResponse(String accessToken, String refreshToken) {
}
