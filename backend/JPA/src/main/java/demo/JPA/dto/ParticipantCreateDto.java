package demo.JPA.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 참여자 개개인의 정보 틀
@Getter
@Setter
@NoArgsConstructor
public class ParticipantCreateDto {
    private Long uuid;
    private String name;
    private String thumbnailUrl;
}