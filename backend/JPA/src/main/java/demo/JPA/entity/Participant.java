package demo.JPA.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"Participant\"")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore // 이 Settlement를 JSON으로 바꿀 때 Member는 무시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;

    @JsonIgnore // 이 Settlement를 JSON으로 바꿀 때 Member는 무시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_member_id")//host의 id를 적어놓는곳
    private Member member;

    @Column(name = "participant_uuid")
    private Long participantUuid;

    @Column(name = "participant_name", nullable = false, length = 50)
    private String participantName;

    @Column(name = "unique_link_token", nullable = false, unique = true)
    private String uniqueLinkToken;

    @Column(name = "participant_thumbnail_url", nullable = false, unique = true)
    private String participantThumbnailUrl;

    @Column(name = "access_link", length = 500)
    private String accessLink;

    @Column(name = "is_submitted", nullable = false)
    private boolean isSubmitted = false;

    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // 참여자의 투표 목록 (양방향)
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    @Builder
    public Participant(Settlement settlement, Member member, String uniqueLinkToken, long participantUuid,
                       String participantName, String participantThumbnailUrl){
        this.settlement = settlement;
        this.member = member;
        this.participantName = participantName;
        this.participantThumbnailUrl = participantThumbnailUrl;
        this.participantUuid = participantUuid;
        this.isSubmitted = false;
        this.uniqueLinkToken = uniqueLinkToken;
    }

    /**
     * 투표 제출을 완료 상태로 변경합니다.
     */
    public void completeSubmission() {
        this.isSubmitted = true;
        this.submittedAt = OffsetDateTime.now();
    }
}