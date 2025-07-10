package demo.JPA.dto;

import java.util.List;

public record VoteSubmitRequestDto(
        List<VoteSubmitChoiceDto> choices
) {}