package demo.JPA.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Builder;

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

    // 기존 필드
    private String title;
    private Long hostMemberId;
    private Integer participantsNum;
    private List<String> imageUrl;

    // ✅ OCR로부터 파싱된 정보들 추가
    private BigDecimal totalAmount;
    private LocalDate receiptDate;
    private LocalTime paymentTime;
    private String storeName;
    private List<OcrItemDto> items;

    // ✅ 내부 클래스: 항목 리스트
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OcrItemDto {
        private String itemName;
        private int quantity;
        private BigDecimal itemPrice;
    }
}
