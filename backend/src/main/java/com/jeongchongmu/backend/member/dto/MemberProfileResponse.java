package com.jeongchongmu.backend.member.dto;

import com.jeongchongmu.backend.member.entity.Member;

public record MemberProfileResponse(
        String nickname,
        String profileImageUrl,
        String thumbnailImageUrl
) {
    public MemberProfileResponse(Member member) {
        this(
                member.getNickname(),
                member.getProfileImageUrl(),
                member.getThumbnailImageUrl()
        );
    }
}
