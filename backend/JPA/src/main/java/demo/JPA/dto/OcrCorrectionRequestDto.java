package demo.JPA.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * OCR 결과 수정 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OcrCorrectionRequestDto {

    // 필수: 어떤 이미지에 대한 수정인지 구분하기 위한 URL
//    private String imageUrl;
    private Long receiptId;

    // 영수증 기본 정보
    private BigDecimal totalAmount;
    private LocalDate receiptDate;
    private LocalTime paymentTime;
    private String storeName;
    private String storeBranch;
    private String bizNum;
    private String address;
    private String tel;

    // 항목 리스트
    private List<ItemDto> items;

    /**
     * 항목 정보
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemDto {
        private String itemName;
        private int quantity;
        private BigDecimal itemPrice;
    }
}