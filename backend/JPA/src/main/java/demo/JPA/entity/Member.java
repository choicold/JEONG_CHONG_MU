package demo.JPA.entity;

import demo.JPA.auth.dto.KakaoUserResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "\"Member\"")
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

    // ✨ [추가] uuid 필드 추가
    @Column(name = "uuid", updatable = false, nullable = false, unique = true)
    private UUID uuid;

    // ✨ [추가] 엔티티 저장 전 UUID 생성
    @PrePersist
    public void createUuid() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
        }
    }

    public Member() {}

    public static Member fromKakaoUSer(KakaoUserResponse kakaoUser) {
        Member member = new Member();
        member.kakaoId = Long.parseLong(kakaoUser.id());

        if (kakaoUser.kakaoAccount() != null) {
            var kakaoAccount = kakaoUser.kakaoAccount();

            if (kakaoAccount.profile() != null) {
                var profile = kakaoAccount.profile();
                member.nickname = profile.nickname();
                member.profileImageUrl = profile.profileImageUrl();
                member.thumbnailImageUrl = profile.thumbnailImageUrl();
                member.isDefaultImage = profile.isDefaultImage();
                member.isDefaultNickname = profile.isDefaultNickname();
            }
        }

        return member;
    }
}