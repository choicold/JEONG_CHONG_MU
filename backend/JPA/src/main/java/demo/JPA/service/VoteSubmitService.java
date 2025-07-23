package demo.JPA.service;
import demo.JPA.dto.AuthenticatedVoteSubmitRequestDto;
import demo.JPA.dto.VoteSubmitRequestDto;
import demo.JPA.entity.*;
import demo.JPA.notification.service.NotificationSendingService;
import demo.JPA.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VoteSubmitService {
    private final SettlementRepository settlementRepository;
    private final ParticipantRepository participantRepository;
    private final VoteRepository voteRepository;
    private final OcrItemRepository ocrItemRepository;
    private final NotificationSendingService notificationSendingService;

    @Transactional
    public String submitVote(UUID uuid, VoteSubmitRequestDto requestDto) {
        Settlement settlement = settlementRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 정산 URL입니다."));

        // 1. 이미 정산이 끝났거나, 정원이 다 찼는지 확인
        long currentParticipants = participantRepository.countBySettlementId(settlement.getId());
        if (currentParticipants >= settlement.getTotalParticipantCount()) {
            throw new IllegalStateException("이미 모든 인원이 투표를 완료했습니다.");
        }

        // 2. 참여자 생성 (DB의 UNIQUE 제약조건이 중복 제출을 막아줌)
        Participant participant = Participant.builder()
                .settlement(settlement)
                .participantName(requestDto.participantName())
                .build();
        try {
            participantRepository.saveAndFlush(participant);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new IllegalStateException("'" + requestDto.participantName() + "' 이름으로 이미 투표가 제출되었습니다.");
        }

        // 3. 투표 내용(Vote) 저장
        for (VoteSubmitRequestDto.Choice choice : requestDto.choices()) {
            OcrItem ocrItem = ocrItemRepository.findById(choice.itemId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다: " + choice.itemId()));

            Vote vote = Vote.builder()
                    .participant(participant)
                    .ocrItem(ocrItem)
                    .isParticipated(choice.isParticipated())
                    .build();
            voteRepository.save(vote);
        }

        // 4. 모든 인원이 투표를 완료했는지 다시 확인
        long finalParticipantCount = participantRepository.countBySettlementId(settlement.getId());
        if (finalParticipantCount == settlement.getTotalParticipantCount()) {
            settlement.setStatus(SettlementStatus.COMPLETED);

            // 총무에게 푸시 알림을 보내기
            notificationSendingService.sendCompletionNotification(settlement);

            return "투표가 제출되었습니다. 모든 인원이 투표를 완료하여 정산이 마감되었습니다!";
        }

        return "투표가 성공적으로 제출되었습니다.";
    }

    @Transactional
    public String submitVote(UUID uuid, AuthenticatedVoteSubmitRequestDto requestDto, Member voter) {
        Settlement settlement = settlementRepository.findByUuid(uuid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 정산 URL입니다."));

        if (settlement.getStatus() != SettlementStatus.VOTING) {
            throw new IllegalStateException("이미 투표가 마감된 정산입니다.");
        }

        Participant participant = Participant.builder()
                .settlement(settlement)
                .participantName(voter.getNickname()) // 닉네임 자동 사용
                .build();
        try {
            participantRepository.saveAndFlush(participant);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new IllegalStateException("'" + voter.getNickname() + "'님은 이미 투표를 제출했습니다.");
        }

        for (AuthenticatedVoteSubmitRequestDto.Choice choice : requestDto.choices()) {
            OcrItem ocrItem = ocrItemRepository.findById(choice.itemId())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 항목입니다: " + choice.itemId()));

            Vote vote = Vote.builder()
                    .participant(participant)
                    .ocrItem(ocrItem)
                    .isParticipated(choice.isParticipated())
                    .build();
            voteRepository.save(vote);
        }

        long finalParticipantCount = participantRepository.countBySettlementId(settlement.getId());
        if (finalParticipantCount == settlement.getTotalParticipantCount()) {
            settlement.setStatus(SettlementStatus.COMPLETED);
            notificationSendingService.sendCompletionNotification(settlement);
            return "투표가 제출되었습니다. 모든 인원이 투표를 완료하여 정산이 마감되었습니다!";
        }

        return "투표가 성공적으로 제출되었습니다.";
    }
}