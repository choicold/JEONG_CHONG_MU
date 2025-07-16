package demo.JPA.notification.service;

import demo.JPA.entity.Member;
import demo.JPA.notification.entity.MemberPushToken;
import demo.JPA.notification.repository.MemberPushTokenRepository;
import demo.JPA.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PushTokenManagementService {

    private final MemberRepository memberRepository;
    private final MemberPushTokenRepository memberPushTokenRepository;

    public void registerToken(Member member, String tokenValue) {
        // 1. 이미 다른 사용자가 이 토큰을 사용 중이라면, 이전 연결을 끊어 소유권을 현재 사용자에게 이전
        memberPushTokenRepository.findByTokenValue(tokenValue)
                .ifPresent(memberPushTokenRepository::delete);

        // 2. 현재 사용자에게 이 토큰이 등록되어 있지 않다면 새로 등록
        boolean tokenExists = member.getPushTokens().stream()
                .anyMatch(token -> token.getTokenValue().equals(tokenValue));
        if (!tokenExists) {
            memberPushTokenRepository.save(new MemberPushToken(member, tokenValue));
        }
    }

    // 로그아웃 시 해당 메서드를 호출하여 pushToken 삭제
    public void deregisterToken(Long memberId, String tokenValue) {
        if (tokenValue == null || tokenValue.isBlank()) return;
        memberPushTokenRepository.deleteByMemberIdAndTokenValue(memberId, tokenValue);
    }
}
