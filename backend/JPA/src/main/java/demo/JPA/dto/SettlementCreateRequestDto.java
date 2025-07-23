package demo.JPA.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementCreateRequestDto {

    // --- 정산 정보 ---
    private String title;
    private Integer participantsNum;
    private OcrDataDto ocrData;
    /**
     * 내부 클래스: OCR 항목 리스트
     * ✨ [수정] JSON 필드명('name', 'count', 'price')과 일치하도록 수정
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OcrItemDto {
        @JsonProperty("name")  // JSON의 'name'을 itemName 필드에 매핑
        private String itemName;

        @JsonProperty("count") // JSON의 'count'를 quantity 필드에 매핑
        private int quantity;

        @JsonProperty("price") // JSON의 'price'를 itemPrice 필드에 매핑
        private BigDecimal itemPrice;
    }

    @Getter
    public static class OcrDataDto {
        // --- 영수증 정보 (OCR 결과) ---
        private List<String> imageUrl; // 영수증이 여러 장일 경우를 대비해 List 유지
        private String storeName;
        private String storeBranch;
        private String bizNum;
        private String address;
        private String tel;
        private LocalDate receiptDate;
        private LocalTime paymentTime;
        private BigDecimal totalAmount;

        // --- 항목 리스트 ---
        private List<OcrItemDto> items;
    }
}