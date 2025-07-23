package demo.JPA.service;

import demo.JPA.dto.SettlementSimpleResponseDto;
import demo.JPA.entity.Member;
import demo.JPA.entity.Settlement;
import demo.JPA.repository.MemberRepository;
import demo.JPA.repository.SettlementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션으로 성능 최적화
public class SettlementListService {

    private final MemberRepository memberRepository;
    private final SettlementRepository settlementRepository;

    // ✨ [수정 또는 추가] 인증된 사용자의 ID를 직접 받아 처리하는 메서드
    public List<SettlementSimpleResponseDto> getSettlementsForAuthenticatedUser(Long userId) {
        // 1. userId로 Member 엔티티를 찾습니다.
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 ID를 가진 멤버를 찾을 수 없습니다: " + userId));

        // 2. Repository의 기존 쿼리 메소드를 호출하여 정산 목록을 가져옵니다.
        List<Settlement> settlements = settlementRepository.findSettlementsByMember(member.getId(), member.getNickname());

        // 3. DTO 목록으로 변환하여 반환합니다.
        return settlements.stream()
                .map(SettlementSimpleResponseDto::new)
                .collect(Collectors.toList());
    }
}