package com.jeongchongmu.backend.vote.service;

import com.jeongchongmu.backend.vote.dto.VoteLoadItemDto;
import com.jeongchongmu.backend.vote.dto.VoteLoadPageDto;
import com.jeongchongmu.backend.vote.dto.VoteLoadReceiptDto;
import com.jeongchongmu.backend.ocr.entity.OcrItem;
import com.jeongchongmu.backend.ocr.entity.OcrReceipt;
import com.jeongchongmu.backend.settlement.entity.Participant;
import com.jeongchongmu.backend.vote.entity.Vote;
import com.jeongchongmu.backend.settlement.repository.ParticipantRepository;
import com.jeongchongmu.backend.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoteLoadService {

    private final ParticipantRepository participantRepository;
    private final VoteRepository voteRepository;

    @Transactional(readOnly = true)
    public VoteLoadPageDto getVotePageData(String token) {
        // 1. 토큰으로 참여자와 정산 정보를 한번에 조회 (최적화)
        Participant participant = participantRepository.findByUniqueLinkTokenWithSettlement(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token: " + token));

        Long participantId = participant.getId();

        // 2. 이 참여자의 모든 VOTE, Item, Receipt 정보를 DB에서 한번에 조회 (최적화)
        List<Vote> allUserVotes = voteRepository.findByParticipantIdWithDetails(participantId);

        // 3. 그룹화 및 DTO 변환 로직은 이전과 동일
        Map<OcrReceipt, List<Vote>> groupedByReceipt = allUserVotes.stream()
                .collect(Collectors.groupingBy(vote -> vote.getOcrItem().getOcrReceipt()));

        List<VoteLoadReceiptDto> receiptGroups = groupedByReceipt.entrySet().stream()
                .map(entry -> {
                    OcrReceipt receipt = entry.getKey();
                    List<VoteLoadItemDto> itemDtos = entry.getValue().stream()
                            .map(vote -> {
                                OcrItem item = vote.getOcrItem();
                                return new VoteLoadItemDto(
                                        item.getId(),
                                        item.getItemName(),
                                        item.getItemPrice().doubleValue(),
                                        item.getQuantity(),
                                        vote.getIsParticipated()
                                );
                            }).toList();
                    return new VoteLoadReceiptDto(receipt.getId(), receipt.getReceiptDate(), itemDtos);
                })
                .toList();

        // 4. 최종 응답 DTO 생성
        return new VoteLoadPageDto(
                participant.getId(),
                participant.getParticipantName(),
                participant.getSettlement().getTitle(),
                receiptGroups
        );
    }
}
