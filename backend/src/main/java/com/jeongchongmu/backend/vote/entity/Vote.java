package com.jeongchongmu.backend.vote.entity;

import com.jeongchongmu.backend.ocr.entity.OcrItem;
import com.jeongchongmu.backend.settlement.entity.Participant;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "\"Vote\"")
public class Vote {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ocr_item_id", nullable = false)
    private OcrItem ocrItem;

    @Column(name = "is_participated", nullable = true)
    private Boolean isParticipated;

    @Column(length = 200)
    private String comment;

    @Column(name = "voted_at", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMPTZ DEFAULT NOW()")
    private OffsetDateTime votedAt;

    @Builder // 빌더 패턴을 사용하기 위해 추가
    public Vote(Participant participant, OcrItem ocrItem, String comment) {
        this.participant = participant;
        this.ocrItem = ocrItem;
        this.comment = comment;
    }
}

