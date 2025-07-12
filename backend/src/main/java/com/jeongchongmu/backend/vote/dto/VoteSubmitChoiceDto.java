package com.jeongchongmu.backend.vote.dto;

public record VoteSubmitChoiceDto(
        Long itemId,
        boolean isAttended
) {}
