package demo.JPA.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotePageLoadDto {

    private String settlementTitle;
    private List<ReceiptGroup> receiptGroups;
    private List<VoteStatusDto> voteStatuses;
    private boolean allVoted;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReceiptGroup {
        private LocalDate receiptDate;
        private BigDecimal totalAmount;
        private String receiptImageUrl;
        private List<ItemInfo> items;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemInfo {
        private Long itemId;
        private String itemName;
        private BigDecimal itemPrice;
        private Integer quantity;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VoteStatusDto {
        private String participantName;
        private boolean hasVoted;
    }
}
