package demo.JPA.service;

import demo.JPA.dto.VoteSubmitRequestDto;
import demo.JPA.entity.Participant;
import demo.JPA.entity.Vote;
import demo.JPA.repository.ParticipantRepository;
import demo.JPA.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteSubmitService {

    private final ParticipantRepository participantRepository;
    private final VoteRepository voteRepository;

    @Transactional
    public void submitVote(String token, VoteSubmitRequestDto requestDto) {
        // 1. 토큰으로 참여자 인증
        Participant participant = participantRepository.findByUniqueLinkTokenWithSettlement(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        // 2. 이미 제출했는지 확인 (선택사항: 재투표를 막고 싶을 경우)
        if (participant.isSubmitted()) {
            throw new IllegalStateException("이미 투표를 제출했습니다.");
        }

        // 3. 제출된 투표 내용으로 Vote 테이블 업데이트
        requestDto.choices().forEach(choice -> {
            Vote vote = voteRepository.findByParticipantIdAndOcrItemId(participant.getId(), choice.itemId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid item ID for this participant"));

            // Vote 엔티티에 Setter가 있다고 가정 (예: setIsParticipated)
            vote.setIsParticipated(choice.isAttended());
        });
        // @Transactional에 의해 변경된 vote 객체들은 메소드 종료 시 자동으로 DB에 업데이트됩니다.

        // 4. 참여자의 제출 상태를 true로 변경
        participant.completeSubmission();
    }
}