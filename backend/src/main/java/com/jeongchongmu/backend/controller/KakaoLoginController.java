package com.jeongchongmu.backend.controller;


import com.jeongchongmu.backend.dto.KakaoTokenResponse;
import com.jeongchongmu.backend.entity.Member;
import com.jeongchongmu.backend.service.KakaoAuthService;
import com.jeongchongmu.backend.service.MemberService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
/*
 * @RestController → 백엔드애서의 테스트를 위한 시각화를 구현하려고 @Controller를 사용했지만,
 * 프론트엔드 화면이 구현되면 이 어노테이션으로 변경해야 함
 * @Controller는 응답 형태가 html인 반면에 @RestController는  JSON/XML 형태이기 때문
 */
@RequestMapping("/login")
public class KakaoLoginController {

    private final KakaoAuthService kakaoAuthService;
    private final MemberService memberService;

    @Value("${kakao.client_id}")
    private String clientId;

    @Value("${kakao.redirect_uri}")
    private String redirectUri;

    public KakaoLoginController(KakaoAuthService kakaoAuthService, MemberService memberService) {
        this.kakaoAuthService = kakaoAuthService;
        this.memberService = memberService;
    }

    /*
     * 로그인 페이지(임시 → 추후 프론트엔드에서 구현)
     * login.html 템플릿에 카카오 로그인 URL 전달
     */
    @GetMapping
    public String loginPage(Model model) {
        // 카카오 로그인 URL 생성
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize" +
                "?response_type=code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri;

        model.addAttribute("location", kakaoAuthUrl);
        return "login"; // login.html 템플릿 반환
    }

    /*
     * 카카오 로그인 콜백 처리
     * 카카오에서 인가 코드와 함께 리다이렉트된 요청을 처리
     */
    @GetMapping("/callback")
    public String kakaoCallback(
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "error_description", required = false) String errorDescription,
            @RequestParam(value = "state", required = false) String state,
            HttpSession session,
            Model model) {

        // 1. 에러 응답 처리
        if (error != null) {
            model.addAttribute("error", "카카오 로그인 실패 : " + errorDescription);
            return "login";
        }

        // 2. 인가 코드 검증
        if (code == null || code.trim().isEmpty()) {
            model.addAttribute("error", "인가 코드가 없습니다.");
            return "login";
        }

        try {
            // 3. 토큰 받기 요청
            KakaoTokenResponse tokenResponseDto = kakaoAuthService.getAccessToken(code);

            // 4. 카카오 사용자 정보로 회원 처리 (기존 회원 확인 및 신규 등록)
            Member member = memberService.processKakaoLogin(tokenResponseDto.getAccessToken());

            // 5. 토큰 정보 세션에 저장
            session.setAttribute("member", member);
            session.setAttribute("memberId", member.getId());
            session.setAttribute("accessToken", tokenResponseDto.getAccessToken());
            session.setAttribute("refreshToken", tokenResponseDto.getRefreshToken());
            session.setAttribute("tokenType", tokenResponseDto.getTokenType());

            // 6. 성공 시 메인 페이지로 리다이렉트
            return "redirect:/main";
        } catch (Exception e) {
            // 7. 토큰 발급 실패 처리
            model.addAttribute("error", "토큰 발급 중 오류 발생 : " + e.getMessage());
            return "login";
        }
    }
}
