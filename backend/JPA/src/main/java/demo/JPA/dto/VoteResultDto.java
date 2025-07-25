package demo.JPA.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 투표 결과 조회용 DTO
 * 각 항목(itemId, itemName, itemPrice)에 대해 참여자 이름 목록(voters)을 포함
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteResultDto {

    private List<ItemVoteResult> items;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ItemVoteResult {
        private Long itemId;
        private String itemName;
        private BigDecimal itemPrice;
        private List<String> voters; // 이 항목을 선택한 사람들의 이름
    }
}
