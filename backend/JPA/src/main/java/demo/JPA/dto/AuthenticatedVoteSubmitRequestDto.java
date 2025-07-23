package demo.JPA.dto;

import java.util.List;

public record AuthenticatedVoteSubmitRequestDto(
        List<Choice> choices
) {
    public record Choice(
            Long itemId,
            Boolean isParticipated
    ) {}
}
