package demo.JPA.dto;

import demo.JPA.entity.Member;

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
