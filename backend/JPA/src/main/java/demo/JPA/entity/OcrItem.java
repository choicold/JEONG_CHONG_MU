package demo.JPA.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter // 연관관계 편의 메서드를 위해 추가
@Builder // 📌 [추가] Builder 어노테이션
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // 📌 [추가]
@Table(name = "\"OCR_Item\"")
public class OcrItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore // 이 Settlement를 JSON으로 바꿀 때 Member는 무시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ocr_receipt_id", nullable = false)
    private OcrReceipt ocrReceipt;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(name = "item_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal itemPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // 항목에 대한 투표 목록 (양방향)
    @OneToMany(mappedBy = "ocrItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();
}