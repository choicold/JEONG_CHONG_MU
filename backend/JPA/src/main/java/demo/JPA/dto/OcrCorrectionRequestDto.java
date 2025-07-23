package demo.JPA.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

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

    /**
     * 정산 검색 결과를 담는 DTO
     */
    @Getter
    @Builder
    public static class SettlementSearchResponseDto {

        // 정산 UUID
        private UUID settlementUuid;

        // 정산 제목
        private String title;

        // 정산 생성 날짜
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private OffsetDateTime createdDate;

        // 가게 이름 (대표 영수증 1개의 가게 이름)
        private String storeName;
    }
}