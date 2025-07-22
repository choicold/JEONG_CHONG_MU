package demo.JPA.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder; // Builder 임포트
import lombok.AllArgsConstructor; // Builder를 위한 AllArgsConstructor 임포트

@Entity
@Getter
@Builder // 📌 [추가] Builder 어노테이션
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor // 📌 [추가] Builder는 모든 필드를 받는 생성자가 필요합니다.
@Table(name = "ocr_receipt")
public class OcrReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @JsonIgnore // 이 Settlement를 JSON으로 바꿀 때 Member는 무시
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", nullable = true)
    private Settlement settlement;

    @Column(name = "receipt_image_url", length = 500)
    private String receiptImageUrl;

    @Column(name = "store_name", length = 100)
    private String storeName;

    @Column(name = "store_branch", length = 100)
    private String storeBranch;

    @Column(name = "biz_num", length = 20)
    private String bizNum;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "tel", length = 20)
    private String tel;

    @Column(name = "payment_time")
    private LocalTime paymentTime;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "receipt_date")
    private LocalDate receiptDate;

    @Column(name = "ocr_processed_at")
    private OffsetDateTime ocrProcessedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private OffsetDateTime createdAt;

    @Setter// 영수증에 속한 항목 목록 (양방향)
    @Builder.Default
    @OneToMany(mappedBy = "ocrReceipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OcrItem> ocrItems = new ArrayList<>();

    public void addOcrItem(OcrItem ocrItem) {
        this.ocrItems.add(ocrItem); // 영수증의 항목 리스트에 아이템을 추가하고,
        ocrItem.setOcrReceipt(this); // 아이템 쪽에도 현재 영수증을 주인으로 설정해줍니다.
    }

    public List<OcrItem> getItems() {
        return this.ocrItems;
    }

    public void setImageUrl(String url) {
        this.receiptImageUrl = url;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    public void setReceiptDate(LocalDate date) {
        this.receiptDate = date;
    }
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public void setStoreBranch(String storeBranch) {
        this.storeBranch = storeBranch;
    }
    public void setBizNum(String bizNum) {
        this.bizNum = bizNum;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setTel(String tel) {
        this.tel = tel;
    }
    public void setPaymentTime(LocalTime time) {
        this.paymentTime = time;
    }

}