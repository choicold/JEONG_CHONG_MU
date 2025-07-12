package demo.JPA.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OCR 결과로부터 추출한 개별 항목(item)의 정보를 담는 DTO 클래스.
 * - 예: 과자, 음료 등 한 줄의 품목 정보를 담는다.
 */
@Data // getter, setter, toString, equals, hashCode 자동 생성
@Builder // 빌더 패턴 사용 가능
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor // 모든 필드 생성자
public class OcrItemDto {

    // 품목 이름 (예: '컵라면')
    private String itemName;

    // 수량 (예: 1개, 2개)
    private int quantity;

    // 가격 (예: 2500원)
    private int price;
}
