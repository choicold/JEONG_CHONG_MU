package com.jeongchongmu.backend.ocr.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeongchongmu.backend.settlement.entity.Settlement;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder // 📌 [추가] Builder 어노테이션
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // 📌 [추가] Builder는 모든 필드를 받는 생성자가 필요합니다.
@Table(name = "\"OCR_Receipt\"")
public class OcrReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore // 이 Settlement를 JSON으로 바꿀 때 Member는 무시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = true)
    private Settlement settlement;

    @Column(name = "receipt_image_url", length = 500)
    private String receiptImageUrl;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "receipt_date")
    private LocalDate receiptDate;

    @Column(name = "ocr_processed_at")
    private OffsetDateTime ocrProcessedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    // 영수증에 속한 항목 목록 (양방향)
    @Builder.Default
    @OneToMany(mappedBy = "ocrReceipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OcrItem> ocrItems = new ArrayList<>();

    public void addOcrItem(OcrItem ocrItem) {
        this.ocrItems.add(ocrItem); // 영수증의 항목 리스트에 아이템을 추가하고,
        ocrItem.setOcrReceipt(this); // 아이템 쪽에도 현재 영수증을 주인으로 설정해줍니다.
    }
}

