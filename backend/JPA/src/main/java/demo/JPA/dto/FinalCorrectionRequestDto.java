package demo.JPA.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

// 총무가 최종 수정을 할 때 사용하는 DTO
@Getter
@NoArgsConstructor
public class FinalCorrectionRequestDto {

    private List<VoteCorrection> voteCorrections;
    private List<ParticipantCorrection> participantCorrections;

    // 특정 참여자의 특정 항목에 대한 투표(체크) 여부를 수정
    @Getter
    @NoArgsConstructor
    public static class VoteCorrection {
        private Long participantId;     // 어떤 참여자의
        private Long itemId;            // 어떤 항목에 대한
        private boolean newIsParticipated; // 새로운 체크 여부 (true: 참여, false: 미참여)
    }

    // 참여자 이름 오타 등을 수정
    @Getter
    @NoArgsConstructor
    public static class ParticipantCorrection {
        private Long participantId;         // 수정할 참여자의 ID
        private String newParticipantName;  // 수정할 참여자의 수정한 이름
    }
}
