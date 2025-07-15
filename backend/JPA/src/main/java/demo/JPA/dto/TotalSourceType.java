package demo.JPA.dto;

public enum TotalSourceType {
    CLOVA_TOTAL,  // Clova OCR 응답에 나온 totalPrice를 사용
    ITEM_SUM,     // 항목 단가 * 수량을 합산해서 사용
    MISSING       // 둘 다 없어서 0원이거나 에러 상태
}