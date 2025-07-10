package demo.JPA.dto;

public record VoteSubmitChoiceDto(
        Long itemId,
        boolean isAttended
) {}