package demo.JPA.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

// 클라이언트로부터 요청을 받을 때 사용할 객체
@Getter//알아서 getter 만들어줌 ㄷㄷ
@NoArgsConstructor
public class SettlementCreateRequestDto {

    ///settlement쪽
    private String title;
    private String description;
    private Long hostMemberId;
    //private OffsetDateTime deadline;

    /// OCR쪽
    //사진 받기.

    /// participant
    private List<ParticipantCreateDto> participants
            = new ArrayList<>();



}