package demo.JPA.service;

import demo.JPA.dto.SettlementSearchResponseDto;
import demo.JPA.entity.Member;
import demo.JPA.entity.OcrReceipt;
import demo.JPA.entity.Settlement;
import demo.JPA.repository.MemberRepository;
import demo.JPA.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementSearchDataService {

    private final SettlementRepository settlementRepository;
    private final MemberRepository memberRepository;

    /**
     * 사용자가 참여한 모든 정산의 검색용 기초 데이터를 조회합니다.
     * @param userId 조회할 사용자의 ID
     * @return 검색 데이터 DTO 리스트
     */
    public List<SettlementSearchResponseDto> getSettlementSearchDataForUser(Long userId) {
        // 1. 사용자 정보 조회
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        // 2. 사용자가 포함된 모든 정산 내역을 가져옵니다.
        List<Settlement> allSettlements = settlementRepository.findSettlementsByMember(member.getId(), member.getNickname());

        // 3. DTO 리스트로 변환하여 반환합니다.
        return allSettlements.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Settlement 엔티티를 SettlementSearchResponseDto로 변환합니다.
     */
    private SettlementSearchResponseDto convertToDto(Settlement settlement) {
        // 가게 이름은 첫 번째 영수증에서 가져오고, 없으면 null로 설정합니다.
        String storeName = settlement.getOcrReceipts().stream()
                .map(OcrReceipt::getStoreName)
                .filter(name -> name != null && !name.isBlank())
                .findFirst()
                .orElse(null);

        return SettlementSearchResponseDto.builder()
                .settlementUuid(settlement.getUuid())
                .title(settlement.getTitle())
                .createdDate(settlement.getCreatedAt())
                .storeName(storeName)
                .build();
    }
}