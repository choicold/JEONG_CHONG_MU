package com.jeongchongmu.backend.entity;

import com.jeongchongmu.backend.dto.KakaoUserResponse;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "members")
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kakao_id", nullable = false, unique = true)
    private Long kakaoId;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "thumbnail_image_url", length = 500)
    private String thumbnailImageUrl;

    @Column(name = "is_default_image")
    private Boolean isDefaultImage;

    @Column(name = "is_default_nickname")
    private Boolean isDefaultNickname;

    @CreatedDate
    @Column(name = "create_at", nullable = false, updatable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    public Member() {}

    public static Member fromKakaoUSer(KakaoUserResponse kakaoUser) {
        Member member = new Member();
        member.kakaoId = Long.parseLong(kakaoUser.getId());

        if (kakaoUser.getKakaoAccount() != null) {
            var kakaoAccount = kakaoUser.getKakaoAccount();

            if (kakaoAccount.getProfile() != null) {
                var profile = kakaoAccount.getProfile();
                member.nickname = profile.getNickname();
                member.profileImageUrl = profile.getProfileImageUrl();
                member.thumbnailImageUrl = profile.getThumbnailImageUrl();
                member.isDefaultImage = profile.isDefaultImage();
                member.isDefaultNickname = profile.isDefaultNickname();
            }
        }

        return member;
    }
}
