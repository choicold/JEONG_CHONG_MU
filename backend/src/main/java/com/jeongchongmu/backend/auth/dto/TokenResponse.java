package com.jeongchongmu.backend.auth.dto;

public record TokenResponse(String accessToken, String refreshToken) {
}