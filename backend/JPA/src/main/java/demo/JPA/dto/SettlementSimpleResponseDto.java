package demo.JPA.dto;

import demo.JPA.entity.Settlement;
import demo.JPA.entity.SettlementStatus;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class SettlementSimpleResponseDto {
    private final UUID id; // settlement.uuid
    private final String title;
    private final SettlementStatus status;
    private final String image_url;
    private final OffsetDateTime created_at;

    public SettlementSimpleResponseDto(Settlement settlement) {
        this.id = settlement.getUuid();
        this.title = settlement.getTitle();
        this.status = settlement.getStatus();
        // 정산에 여러 영수증이 있을 수 있으므로, 첫 번째 영수증의 이미지를 대표 이미지로 사용
        this.image_url = settlement.getOcrReceipts().isEmpty() ? null : settlement.getOcrReceipts().get(0).getReceiptImageUrl();
        this.created_at = settlement.getCreatedAt();
    }
}