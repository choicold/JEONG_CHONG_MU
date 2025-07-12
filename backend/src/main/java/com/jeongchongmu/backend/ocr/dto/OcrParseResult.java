package com.jeongchongmu.backend.ocr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Clova OCR 응답을 바탕으로 파싱된 전체 영수증 정보를 담는 DTO 클래스.
 * - 총 금액, 날짜, 품목 리스트 등을 포함한다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrParseResult {

    private String imageUrl; // 📌 [수정] imageUrl 필드 추가
    private LocalDate receiptDate;
    private BigDecimal totalAmount; // 📌 [수정] totalAmount 필드 추가 (BigDecimal 타입)
    private List<OcrItemDto> items;

    // 내부 static 클래스로 DTO를 정의하면 더 좋습니다.
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OcrItemDto {
        private String itemName;
        private int quantity;
        private BigDecimal itemPrice; // 📌 [수정] itemPrice 타입을 BigDecimal로 통일
    }
}
