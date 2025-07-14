package demo.JPA.dto;
import java.util.List;

public record VoteSubmitRequestDto(
        String participantName,
        List<Choice> choices
) {
    public record Choice(
            Long itemId,
            Boolean isParticipated
    ) {}
}