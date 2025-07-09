package com.jeongchongmu.backend.dto;

import com.jeongchongmu.backend.entity.Member;
import lombok.Getter;

@Getter
public class MemberProfileResponse {
    private final String nickname;
    private final String profileImageUrl;
    private final String thumbnailImageUrl;

    public MemberProfileResponse(Member member) {
        this.nickname = member.getNickname();
        this.profileImageUrl = member.getProfileImageUrl();
        this.thumbnailImageUrl = member.getThumbnailImageUrl();
    }
}
