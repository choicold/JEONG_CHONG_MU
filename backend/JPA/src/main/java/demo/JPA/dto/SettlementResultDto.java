package demo.JPA.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 최종 정산 결과를 담는 DTO.
 * 전체 정산 정보와 참여자별 개별 결과를 포함
 */
@Getter
@Builder
public class SettlementResultDto {
    private String settlementTitle;
    private String hostName;
    private BigDecimal totalAmount;
    private List<ParticipantResult> participantResults;

    // 참여자 한 명의 최종 정산 결과를 담는 내부 DTO
    @Getter
    @Builder
    public static class ParticipantResult {
        private String participantName;
        private BigDecimal amountToPay;
        private String tossPayLink;
    }
}
