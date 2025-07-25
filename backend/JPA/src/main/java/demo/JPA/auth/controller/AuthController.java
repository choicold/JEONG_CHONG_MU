package demo.JPA.auth.controller;

import demo.JPA.auth.dto.KakaoLoginRequest;
import demo.JPA.auth.dto.RefreshTokenRequest;
import demo.JPA.auth.dto.TokenResponse;
import demo.JPA.auth.service.AuthService;
import demo.JPA.notification.dto.PushTokenRequestDto;
import demo.JPA.config.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 이 api는 프론트에서 구체화해야 하는 부분이므로, 나중에 합치면 삭제해야함.
    @GetMapping("/login/callback")
    public ResponseEntity<String> kakaoLoginCallback(@RequestParam("code") String code) {
        String responseBody = "카카오 인증완료 : 코드값 : " + code;
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/kakao")
    public ResponseEntity<TokenResponse> kakaoLogin(@RequestBody KakaoLoginRequest request) {
        TokenResponse tokenResponse = authService.kakaoLogin(request);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        System.out.println("리프레쉬\n");
        TokenResponse tokenResponse = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        authService.logout(principalDetails.getMember().getId());
        return ResponseEntity.ok().build();
    }
}
