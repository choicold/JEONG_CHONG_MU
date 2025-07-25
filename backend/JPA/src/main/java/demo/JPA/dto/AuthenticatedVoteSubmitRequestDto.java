package demo.JPA.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class AuthenticatedVoteSubmitRequestDto {

    private List<Choice> choices;

    @Getter
    @NoArgsConstructor
    public static class Choice {
        private Long itemId;
        private Boolean isParticipated;
    }
}