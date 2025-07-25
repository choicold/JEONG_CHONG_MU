package demo.JPA.service;

import demo.JPA.dto.ParticipantChoicesResponseDto;
import demo.JPA.dto.VotePageLoadDto;
import demo.JPA.entity.OcrReceipt;
import demo.JPA.entity.Settlement;
import demo.JPA.repository.OcrReceiptRepository;
import demo.JPA.repository.ParticipantRepository;
import demo.JPA.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VotePageLoadService {
    private final SettlementRepository settlementRepository;
    private final OcrReceiptRepository ocrReceiptRepository; // OcrItemRepository 대신 사용
    private final ParticipantRepository participantRepository;

    @Transactional(readOnly = true)
    public VotePageLoadDto getVotePageData(UUID uuid) {
        // 1. UUID로 정산 정보 조회
        Settlement settlement = settlementRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 정산 URL입니다."));

        // 2. 정산 ID를 통해 모든 'OcrReceipt'와 관련 'OcrItem'들을 한번에 조회
        List<OcrReceipt> receipts = ocrReceiptRepository.findAllWithItemsBySettlementId(settlement.getId());

        // 3. 조회된 엔티티들을 새로운 DTO 구조로 변환
        List<VotePageLoadDto.ReceiptGroup> receiptGroups = receipts.stream()
                .map(receipt -> {
                    List<VotePageLoadDto.ItemInfo> itemInfos = receipt.getOcrItems().stream()
                            .map(item -> new VotePageLoadDto.ItemInfo(
                                    item.getId(),
                                    item.getItemName(),
                                    item.getItemPrice(),
                                    item.getQuantity()))
                            .collect(Collectors.toList());

                    return new VotePageLoadDto.ReceiptGroup(
                            receipt.getReceiptDate(),
                            receipt.getTotalAmount(),
                            receipt.getReceiptImageUrl(),
                            itemInfos);
                })
                .collect(Collectors.toList());

        return new VotePageLoadDto(settlement.getTitle(), receiptGroups);
    }

    /**
     * ✨ [추가] 특정 참여자의 기존 투표 선택지를 불러오는 로직
     */
    @Transactional(readOnly = true)
    public ParticipantChoicesResponseDto getParticipantChoices(UUID settlementUuid, String participantName) {
        Settlement settlement = settlementRepository.findByUuid(settlementUuid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 정산 URL입니다."));

        // 이름으로 참여자를 찾음
        return participantRepository.findBySettlementIdAndParticipantName(settlement.getId(), participantName)
                .map(participant -> {
                    // 참여자가 있으면, 투표 내역을 DTO로 변환하여 반환
                    List<ParticipantChoicesResponseDto.Choice> choices = participant.getVotes().stream()
                            .map(vote -> new ParticipantChoicesResponseDto.Choice(vote.getOcrItem().getId(), vote.getIsParticipated()))
                            .collect(Collectors.toList());
                    return new ParticipantChoicesResponseDto(choices);
                })
                // 참여자가 없으면 빈 목록을 가진 DTO 반환
                .orElse(new ParticipantChoicesResponseDto(Collections.emptyList()));
    }
}