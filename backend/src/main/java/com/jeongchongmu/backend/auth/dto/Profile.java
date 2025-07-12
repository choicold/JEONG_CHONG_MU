package com.jeongchongmu.backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Profile(
        String nickname,

        @JsonProperty("thumbnail_image_url")
        String thumbnailImageUrl,

        @JsonProperty("profile_image_url")
        String profileImageUrl,

        @JsonProperty("is_default_image")
        boolean isDefaultImage,

        @JsonProperty("is_default_nickname")
        boolean isDefaultNickname
) {
}