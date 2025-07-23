package demo.JPA.entity;

import demo.JPA.auth.dto.KakaoUserResponse;
import demo.JPA.notification.entity.MemberPushToken;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "member")
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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MemberPushToken> pushTokens = new ArrayList<>();

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

    // 카카오 로그인으로 회원가입 이후 로그인 시 입력받아야할 계좌정보
    @Column(name = "bank_name", length = 50)
    private String bankName;

    @Column(name = "account_number", length = 100)
    private String accountNumber;

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