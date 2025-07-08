package com.jeongchongmu.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserResponse {

    private String id;

    @JsonProperty("has_signed_up")
    private boolean hasSignedUp;

    @JsonProperty("connected_at")
    private String connectedAt;

    @JsonProperty("synched_at")
    private String synchedAt;

    private Map<String, Object> properties;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @JsonProperty("for_partner")
    private Partner forPartner;
}
