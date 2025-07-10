package demo.JPA.dto;

import java.util.List;

public record VoteLoadPageDto(
        Long participantId,//앤 안씀
        String participantName,//채무자 이름
        String settlementTitle,//정산 title
        List<VoteLoadReceiptDto> receiptGroups // 영수증들
) {}