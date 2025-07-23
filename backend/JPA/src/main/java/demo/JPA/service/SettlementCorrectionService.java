package demo.JPA.service;

import demo.JPA.dto.FinalCorrectionRequestDto;
import demo.JPA.entity.Participant;
import demo.JPA.entity.Vote;
import demo.JPA.repository.ParticipantRepository;
import demo.JPA.repository.VoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SettlementCorrectionService {

    private final ParticipantRepository participantRepository;
    private final VoteRepository voteRepository;

    public void applyFinalCorrections(FinalCorrectionRequestDto requestDto) {
        // 1. 투표(체크) 여부 수정
        if (requestDto.getVoteCorrections() != null) {
            for (FinalCorrectionRequestDto.VoteCorrection correction : requestDto.getVoteCorrections()) {
                // 특정 참여자의 특정 항목에 대한 투표 정보를 찾습니다.
                Vote vote = voteRepository.findByParticipantIdAndOcrItemId(
                                correction.getParticipantId(),
                                correction.getItemId())
                        .orElseThrow(() -> new EntityNotFoundException("수정할 투표 정보를 찾을 수 없습니다."));

                // ✨ isParticipated 필드 체크 여부만 수정
                vote.setIsParticipated(correction.isNewIsParticipated());
            }
        }

        // 2. 참여자 이름 수정
        if (requestDto.getParticipantCorrections() != null) {
            for (FinalCorrectionRequestDto.ParticipantCorrection correction : requestDto.getParticipantCorrections()) {
                Participant participant = participantRepository.findById(correction.getParticipantId())
                        .orElseThrow(() -> new EntityNotFoundException("참여자를 찾을 수 없습니다: ID " + correction.getParticipantId()));

                // 이름(participantName)만 수정
                participant.updateName(correction.getNewParticipantName());
            }
        }
    }
}
