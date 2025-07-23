package demo.JPA.controller;

import demo.JPA.dto.MemberProfileResponse;
import demo.JPA.dto.SettlementSimpleResponseDto; // ✨ [추가] import
import demo.JPA.entity.Member;
import demo.JPA.service.MemberService;
import demo.JPA.service.SettlementListService; // ✨ [추가] import
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; // ✨ [추가] import
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List; // ✨ [추가] import
import java.util.UUID;   // ✨ [추가] import

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final SettlementListService settlementListService; // ✨ [추가] 정산 목록 서비스 주입

    // (기존 코드) 로그인한 사용자의 프로필 정보를 조회하는 API
    @GetMapping("/me")
    public ResponseEntity<MemberProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails user) {
        Long memberId = Long.parseLong(user.getUsername());

        Member member = memberService.findById(memberId)
                .orElseThrow(() -> new RuntimeException("id로 Member 조회 실패: " + memberId));

        return ResponseEntity.ok(new MemberProfileResponse(member));
    }
}