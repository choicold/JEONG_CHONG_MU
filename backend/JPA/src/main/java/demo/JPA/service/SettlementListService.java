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

    public List<SettlementSimpleResponseDto> getSettlementsForMember(UUID memberUuid) {
        // 1. memberUuid를 사용하여 Member 엔티티를 찾습니다.
        Member member = memberRepository.findByUuid(memberUuid)
                .orElseThrow(() -> new EntityNotFoundException("해당 UUID를 가진 멤버를 찾을 수 없습니다: " + memberUuid));

        // 2. Repository에 추가한 쿼리 메소드를 호출하여 정산 목록을 가져옵니다.
        List<Settlement> settlements = settlementRepository.findSettlementsByMember(member.getId(), member.getNickname());

        // 3. 조회된 Settlement 엔티티 목록을 SettlementSimpleResponseDto 목록으로 변환합니다.
        return settlements.stream()
                .map(SettlementSimpleResponseDto::new) // DTO의 생성자를 사용하여 변환
                .collect(Collectors.toList());
    }
}