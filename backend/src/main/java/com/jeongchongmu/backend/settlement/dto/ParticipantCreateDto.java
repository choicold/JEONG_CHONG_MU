package com.jeongchongmu.backend.settlement.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParticipantCreateDto {
    private Long uuid;
    private String name;
    private String thumbnailUrl;
}
