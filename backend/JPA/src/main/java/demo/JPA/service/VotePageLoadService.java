package demo.JPA.service;

import demo.JPA.dto.ParticipantChoicesResponseDto;
import demo.JPA.dto.VotePageLoadDto;
import demo.JPA.dto.VoteResultDto;
import demo.JPA.entity.OcrReceipt;
import demo.JPA.entity.Settlement;
import demo.JPA.repository.OcrReceiptRepository;
import demo.JPA.repository.ParticipantRepository;
import demo.JPA.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VotePageLoadService {
    private final SettlementRepository settlementRepository;
    private final OcrReceiptRepository ocrReceiptRepository;
    private final ParticipantRepository participantRepository;

    @Transactional(readOnly = true)
    public VotePageLoadDto getVotePageData(UUID uuid) {
        Settlement settlement = settlementRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 정산 URL입니다."));

        List<OcrReceipt> receipts = ocrReceiptRepository.findAllWithItemsBySettlementId(settlement.getId());

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

        List<VotePageLoadDto.VoteStatusDto> voteStatuses = participantRepository.findBySettlementId(settlement.getId())
                .stream()
                .map(p -> new VotePageLoadDto.VoteStatusDto(p.getParticipantName(), !p.getVotes().isEmpty()))
                .collect(Collectors.toList());

        boolean allVoted = voteStatuses.size() == settlement.getTotalParticipantCount()
                && voteStatuses.stream().allMatch(VotePageLoadDto.VoteStatusDto::hasVoted);

        return new VotePageLoadDto(
                settlement.getTitle(),
                receiptGroups,
                voteStatuses,
                allVoted
        );
    }

    @Transactional(readOnly = true)
    public ParticipantChoicesResponseDto getParticipantChoices(UUID settlementUuid, String participantName) {
        Settlement settlement = settlementRepository.findByUuid(settlementUuid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 정산 URL입니다."));

        return participantRepository.findBySettlementIdAndParticipantName(settlement.getId(), participantName)
                .map(participant -> {
                    var choices = participant.getVotes().stream()
                            .map(vote -> new ParticipantChoicesResponseDto.Choice(
                                    vote.getOcrItem().getId(),
                                    vote.getIsParticipated()))
                            .toList();
                    return new ParticipantChoicesResponseDto(choices);
                })
                .orElse(new ParticipantChoicesResponseDto(Collections.emptyList()));
    }

    @Transactional(readOnly = true)
    public VoteResultDto getVoteResults(UUID settlementUuid) {
        Settlement settlement = settlementRepository.findByUuid(settlementUuid)
                .orElseThrow(() -> new IllegalArgumentException("정산 정보 없음"));

        List<OcrReceipt> receipts = ocrReceiptRepository.findAllWithItemsBySettlementId(settlement.getId());

        List<VoteResultDto.ItemVoteResult> itemVoteResults = receipts.stream()
                .flatMap(receipt -> receipt.getOcrItems().stream())
                .map(item -> {
                    List<String> voters = item.getVotes().stream()
                            .filter(v -> Boolean.TRUE.equals(v.getIsParticipated()))
                            .map(v -> v.getParticipant().getParticipantName())
                            .toList();
                    return new VoteResultDto.ItemVoteResult(
                            item.getId(),
                            item.getItemName(),
                            item.getItemPrice(),
                            voters
                    );
                })
                .toList();

        return new VoteResultDto(itemVoteResults);
    }
}
