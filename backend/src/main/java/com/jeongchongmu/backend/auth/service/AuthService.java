package com.jeongchongmu.backend.auth.service;

import com.jeongchongmu.backend.config.security.JwtTokenProvider;
import com.jeongchongmu.backend.auth.dto.TokenResponse;
import com.jeongchongmu.backend.member.entity.Member;

import com.jeongchongmu.backend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    public TokenResponse kakaoLogin(String kakaoAccessToken) {
        // 1. 카카오 Access Token으로 회원가입 또는 로그인 처리
        Member member = memberService.processKakaoLogin(kakaoAccessToken);

        // 2. Spring Security용 Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                member.getId(), // principal은 앱 서비스의 user_id
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")) // 기본 권한
        );

        // 3. 앱 서비스의 Access Token 및 Refresh Token 생성
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 4. Redis에 Refresh Token 저장 및 만료 시간 설정
        redisTemplate.opsForValue().set(
                "Refresh Token: " + member.getId(),
                refreshToken,
                jwtTokenProvider.getRefreshTokenValidityInMilliSeconds(),
                TimeUnit.MILLISECONDS
        );

        return new TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        // 1. Refresh Token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid Refresh Token");
        }

        // 2. 토큰에서 사용자 정보(ID) 가져오기
        Authentication authentication = jwtTokenProvider.getAuthentication(refreshToken);
        String memberId = authentication.getName();

        // 3. Redis에 저장된 Refresh Token과 일치하는지 확인
        String savedRefreshToken = redisTemplate.opsForValue().get("Refresh Token: " + memberId);
        if (!refreshToken.equals(savedRefreshToken)) {
            throw new RuntimeException("Mismatched Refresh Token");
        }

        // 4. 새로운 Access Token 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);

        return new TokenResponse(newAccessToken, null);
    }

    public void logout(String memberId) {
        // Redis에서 해당 유저의 Refresh Token 삭제
        redisTemplate.opsForValue().get("Refresh Token: " + memberId);
    }
}
