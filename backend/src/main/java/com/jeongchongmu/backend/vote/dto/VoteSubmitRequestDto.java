package com.jeongchongmu.backend.vote.dto;

import java.util.List;

public record VoteSubmitRequestDto(
        List<VoteSubmitChoiceDto> choices
) {}
