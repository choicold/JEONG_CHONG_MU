package com.jeongchongmu.backend.controller;

import com.jeongchongmu.backend.dto.MemberProfileResponse;
import com.jeongchongmu.backend.entity.Member;
import com.jeongchongmu.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 로그인한 사용자의 프로필 정보를 조회하는 API
    @GetMapping("/me")
    public ResponseEntity<MemberProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails user) {
        // @AuthenticationalPrincipal을 통해 JWT 토큰의 사용자 정보(ID)를 가져옴
        Long memberId = Long.parseLong(user.getUsername());

        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new RuntimeException("id로 Member 조회 실패: " + memberId));

        return ResponseEntity.ok(new MemberProfileResponse(member));
    }
}
