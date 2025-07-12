package com.jeongchongmu.backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUserResponse(
        String id,
        @JsonProperty("has_signed_up")
        boolean hasSignedUp,
        @JsonProperty("connected_at")
        String connectedAt,
        @JsonProperty("synched_at")
        String synchedAt,
        Map<String, Object> properties,
        @JsonProperty("kakao_account")
        KakaoAccount kakaoAccount,
        @JsonProperty("for_partner")
        Partner forPartner
) {
}