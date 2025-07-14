package demo.JPA.service;

import demo.JPA.dto.SettlementDetailResponseDto;
import demo.JPA.entity.OcrReceipt;
import demo.JPA.entity.Participant;
import demo.JPA.entity.Settlement;
import demo.JPA.repository.OcrReceiptRepository;
import demo.JPA.repository.ParticipantRepository;
import demo.JPA.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementDetailService {

    private final SettlementRepository settlementRepository;
    private final ParticipantRepository participantRepository;
    private final OcrReceiptRepository ocrReceiptRepository;

    public SettlementDetailResponseDto getSettlementDetail(UUID settlementUuid) {
        // 1. UUID로 Settlement 조회 (없으면 예외 발생)
        Settlement settlement = settlementRepository.findByUuid(settlementUuid)
                .orElseThrow(() -> new EntityNotFoundException("해당 UUID의 정산을 찾을 수 없습니다: " + settlementUuid));

        // 2. Settlement 에 연관된 참여자 목록 조회
        List<Participant> participants = participantRepository.findBySettlementId(settlement.getId());

        // 3. Settlement 에 연관된 영수증 조회 (첫 번째 영수증을 대표로 사용)
        // 만약 여러 영수증 중 특정 영수증을 선택해야 한다면 로직 수정이 필요합니다.
        OcrReceipt receipt = ocrReceiptRepository.findFirstBySettlementIdOrderByCreatedAtDesc(settlement.getId())
                .orElse(null); // 영수증이 없을 수도 있으므로 orElse(null) 처리

        // 4. 조회된 엔티티들을 DTO로 변환하여 반환
        return SettlementDetailResponseDto.from(settlement, participants, receipt);
    }
}