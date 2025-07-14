package demo.JPA.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "\"Participant\"",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_participant_in_settlement",
                columnNames = {"settlement_id", "participant_name"}
        )
)
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = false)
    private Settlement settlement;


    @Column(name = "participant_name", nullable = false, length = 50)
    private String participantName;


    @Column(name = "submitted_at")
    private OffsetDateTime submittedAt;

    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();

    @Builder
    public Participant(Settlement settlement, String participantName) {
        this.settlement = settlement;
        this.participantName = participantName;
        this.submittedAt = OffsetDateTime.now(); // 생성 시 제출된 것으로 간주
    }
}