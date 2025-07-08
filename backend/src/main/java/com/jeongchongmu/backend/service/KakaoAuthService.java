package com.jeongchongmu.backend.service;

import com.jeongchongmu.backend.dto.KakaoTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoAuthService {
    @Value("${kakao.client_id}")
    private String client_id;

    @Value("${kakao.redirect_uri}")
    private String redirect_uri;

    @Value("${kakao.token_uri}")
    private String token_uri;

    private final RestTemplate restTemplate;

    public KakaoAuthService() {
        this.restTemplate = new RestTemplate();
    }

    /*
     * 카카오 토큰 받기 → 토큰은 POST로 요청
     * @param code 인가 코드 받로 얻은 인가 코드
     * @return 카카오 토큰 응답
     */
    public KakaoTokenResponse getAccessToken(String code) {
        // 1. 토큰 요청 헤더 설정 - Content-Type은 application/x-www-form-urlencoded 필수
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 2. 토큰 요청 본문 설정 - 공식 문서 파라미터
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", client_id);
        body.add("redirect_uri", redirect_uri);
        body.add("code", code);

        // 3. HTTP POST 요청 엔티티 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            // 4. POST 요청 보내기
            ResponseEntity<KakaoTokenResponse> response = restTemplate.postForEntity(
                    token_uri,
                    request,
                    KakaoTokenResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            // 토큰 발급 실패 처리
            throw new RuntimeException("카카오 토큰 발급 실패 : " + e.getMessage(), e);
        }
    }
}
