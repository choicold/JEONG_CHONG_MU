package com.jeongchongmu.backend.controller;

import com.jeongchongmu.backend.dto.KakaoLoginRequest;
import com.jeongchongmu.backend.dto.RefreshTokenRequest;
import com.jeongchongmu.backend.dto.TokenResponse;
import com.jeongchongmu.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao")
    public ResponseEntity<TokenResponse> kakaoLogin(@RequestBody KakaoLoginRequest request) {
        TokenResponse tokenResponse = authService.kakaoLogin(request.getAccessToken());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        TokenResponse tokenResponse = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserDetails user) {
        authService.logout(user.getUsername());
        return ResponseEntity.ok().build();
    }
}
