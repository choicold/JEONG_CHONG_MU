package demo.JPA.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

// 레코드를 사용하여 불변 데이터 객체를 간결하게 정의
public record VotePageLoadDto(
        String settlementTitle,
        List<ReceiptGroup> receiptGroups // items 리스트 대신 receiptGroups 리스트를 포함
) {
    // 각 영수증 그룹을 나타내는 DTO
    public record ReceiptGroup(
            LocalDate receiptDate,
            BigDecimal totalAmount,
            String receiptImageUrl,
            List<ItemInfo> items
    ) {}

    // 각 항목 정보를 나타내는 DTO
    public record ItemInfo(
            Long itemId,
            String itemName,
            BigDecimal itemPrice,
            Integer quantity
    ) {}
}