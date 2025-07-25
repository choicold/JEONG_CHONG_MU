package demo.JPA.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class ParticipantChoicesResponseDto {

    private List<Choice> choices;

    public ParticipantChoicesResponseDto(List<Choice> choices) {
        this.choices = choices;
    }

    // 내부 DTO
    @Getter
    @NoArgsConstructor
    public static class Choice {
        private Long itemId;
        private Boolean isParticipated;

        public Choice(Long itemId, Boolean isParticipated) {
            this.itemId = itemId;
            this.isParticipated = isParticipated;
        }
    }
}