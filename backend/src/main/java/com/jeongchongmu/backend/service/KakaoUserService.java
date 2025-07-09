package com.jeongchongmu.backend.service;

import com.jeongchongmu.backend.dto.KakaoUserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoUserService {
    @Value("${kakao.client_id}")
    private String client_id;

    @Value("${kakao.user_info_uri}")
    private String userInfoUri;

    private final RestTemplate restTemplate;

    public KakaoUserService() {
        this.restTemplate = new RestTemplate();
    }

    // 엑세스 토큰으로 카카오 사용자 정보 조회
    public KakaoUserResponse getUserInfo(String accessToken) {
        // 1. 헤더에 액세스 토큰 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        // 2. HTTP 요청 엔티티 생성
        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            // 3. 카카오 API 호출
            ResponseEntity<KakaoUserResponse> response = restTemplate.exchange(
                    userInfoUri,
                    HttpMethod.GET,
                    request,
                    KakaoUserResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("카카오 사용자 정보 조회 실패 : " + e.getMessage());
        }
    }
}
