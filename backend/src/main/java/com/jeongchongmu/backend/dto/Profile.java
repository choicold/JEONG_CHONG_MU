package com.jeongchongmu.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile {
    // 닉네임
    private String nickname;

    // 프로필 미리보기 이미지 URL
    @JsonProperty("thumbnail_image_url")
    private String thumbnailImageUrl;

    // 프로필 사진 URL
    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    // 프로필 사진이 기본 프로필 사진 URL인지 여부
    @JsonProperty("is_default_image")
    private boolean isDefaultImage;

    // 닉네임이 기본 닉네임("닉네임을 등록해주세요")인지 여부
    @JsonProperty("is_default_nickname")
    private boolean isDefaultNickname;
}
