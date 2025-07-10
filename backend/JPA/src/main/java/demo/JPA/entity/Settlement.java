package demo.JPA.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Entity
@Getter
@Setter//지우기
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"Settlement\"")
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 180)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @JsonIgnore // 이 Settlement를 JSON으로 바꿀 때 Member는 무시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_member_id", nullable = false)
    private Member hostMember;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)//enum문제 해결
    @Column(name = "status", nullable = false)
    private SettlementStatus status;

    @Column(name = "deadline")
    private OffsetDateTime deadline;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Settlement에 속한 영수증 목록 (양방향)
    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OcrReceipt> ocrReceipts = new ArrayList<>();

    // Settlement에 속한 참여자 목록 (양방향)
    @OneToMany(mappedBy = "settlement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    // 빌더 패턴을 위한 생성자
    @Builder
    public Settlement(String title, String description, Member hostMember
                      //, OffsetDateTime deadline
                        ) {
        this.title = title;
        this.description = description;
        this.hostMember = hostMember;
        this.status = SettlementStatus.ACTIVE; // 생성 시 기본값 설정
        //this.deadline = deadline;
    }
}