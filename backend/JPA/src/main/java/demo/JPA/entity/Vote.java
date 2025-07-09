package demo.JPA.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"Vote\"", uniqueConstraints = {
        @UniqueConstraint(
                name = "participant_item_unique",
                columnNames = {"participant_id", "ocr_item_id"}
        )
})
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore // 이 Settlement를 JSON으로 바꿀 때 Member는 무시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @JsonIgnore // 이 Settlement를 JSON으로 바꿀 때 Member는 무시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ocr_item_id", nullable = false)
    private OcrItem ocrItem;

    @Column(name = "is_participated", nullable = false)
    private boolean isParticipated;

    @Column(name = "comment", length = 200)
    private String comment;

    @CreationTimestamp
    @Column(name = "voted_at", nullable = false, updatable = false)
    private OffsetDateTime votedAt;
}