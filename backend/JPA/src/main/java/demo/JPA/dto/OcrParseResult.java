package demo.JPA.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    // 영수증 날짜 (예: "2024-07-10")
    private String receiptDate;

    // 총 결제 금액 (예: 15400원)
    private int totalPrice;

    // 품목 리스트 (OcrItemDto 객체 리스트)
    private List<OcrItemDto> items;
}
