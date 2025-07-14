package demo.JPA.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"Member\"")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // [수정] DDL에 맞춰 'name' -> 'nickname'으로 변경
    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    // [수정] DDL에 맞춰 'nullable = false' 추가
    @Column(name = "kakao_id", unique = true, nullable = false)
    private Long kakaoId;

    // [추가] DDL에 있는 컬럼들 추가
    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Column(name = "thumbnail_image_url", length = 500)
    private String thumbnailImageUrl;

    @Column(name = "is_default_image")
    private Boolean isDefaultImage;

    @Column(name = "is_default_nickname")
    private Boolean isDefaultNickname;

    // [수정] DDL에 맞춰 컬럼 이름 'created_at' -> 'create_at'으로 변경
    @CreationTimestamp
    @Column(name = "create_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // [수정] DDL에 맞춰 컬럼 이름 'updated_at' -> 'update_at'으로 변경
    @UpdateTimestamp
    @Column(name = "update_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Member가 주최하는 정산 목록 (양방향 관계는 그대로 유지)
    @OneToMany(mappedBy = "hostMember", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Settlement> hostedSettlements = new ArrayList<>();

}