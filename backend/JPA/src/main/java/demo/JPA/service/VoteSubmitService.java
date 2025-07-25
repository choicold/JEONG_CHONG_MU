package demo.JPA.service;

import demo.JPA.dto.VoteSubmitRequestDto;
import demo.JPA.entity.*;
import demo.JPA.notification.service.NotificationSendingService;
import demo.JPA.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoteSubmitService {

    private final SettlementRepository settlementRepository;
    private final ParticipantRepository participantRepository;
    private final VoteRepository voteRepository;
    private final OcrItemRepository ocrItemRepository;
    private final NotificationSendingService notificationSendingService;

    // ✨ [최종] 총무, 일반 참가자 모두를 위한 단일 투표 제출/수정 메서드
    @Transactional
    public String submitVote(UUID uuid, VoteSubmitRequestDto requestDto) {
        Settlement settlement = settlementRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 정산 URL입니다."));

        // 이름으로 기존 참여자인지 확인 (총무도 자신의 닉네임으로 참여)
        Participant participant = participantRepository.findBySettlementIdAndParticipantName(settlement.getId(), requestDto.participantName())
                .orElse(null);

        if (participant != null) {
            // --- 기존 참여자일 경우: 투표 내역 UPDATE ---
            for (VoteSubmitRequestDto.Choice choice : requestDto.choices()) {
                Vote vote = voteRepository.findByParticipantIdAndOcrItemId(participant.getId(), choice.itemId())
                        .orElseThrow(() -> new IllegalArgumentException("잘못된 항목 ID입니다: " + choice.itemId()));

                vote.setIsParticipated(choice.isParticipated()); // 선택 업데이트
            }
            participant.setSubmittedAt(OffsetDateTime.now()); // 수정 시간 업데이트
            return "투표 내용이 성공적으로 수정되었습니다.";

        } else {
            // --- 신규 참여자일 경우: 기존 INSERT 로직 수행 ---
            long currentParticipants = participantRepository.countBySettlementId(settlement.getId());
            if (currentParticipants >= settlement.getTotalParticipantCount()) {
                throw new IllegalStateException("이미 모든 인원이 투표를 완료했습니다.");
            }

            Participant newParticipant = Participant.builder()
                    .settlement(settlement)
                    .participantName(requestDto.participantName())
                    .build();
            participantRepository.save(newParticipant);

            for (VoteSubmitRequestDto.Choice choice : requestDto.choices()) {
                OcrItem ocrItem = ocrItemRepository.findById(choice.itemId())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다: " + choice.itemId()));
                Vote vote = Vote.builder()
                        .participant(newParticipant)
                        .ocrItem(ocrItem)
                        .isParticipated(choice.isParticipated())
                        .build();
                voteRepository.save(vote);
            }

            // 모든 인원이 투표 완료했는지 다시 확인
            long finalParticipantCount = participantRepository.countBySettlementId(settlement.getId());
            if (finalParticipantCount == settlement.getTotalParticipantCount()) {
                notificationSendingService.sendCompletionNotification(settlement);
                return "투표가 제출되었습니다. 모든 인원이 투표를 완료하여 정산이 마감되었습니다!";
            }
            return "투표가 성공적으로 제출되었습니다.";
        }
    }
}