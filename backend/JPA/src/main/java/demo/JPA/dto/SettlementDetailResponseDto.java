package demo.JPA.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import demo.JPA.entity.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class SettlementDetailResponseDto {

    private String id; // Settlement.uuid
    private String title; // Settlement.title

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private OffsetDateTime date; // Settlement.created_at

    private String status; // Settlement.status 를 한글로 변환
    private int memberCount; // Settlement.total_participant_count
    private String receiptImg; // OCR_Receipt.receipt_image_url
    private OcrResultDto resultOfOCR;
    private List<String> participants;
    private String votePageLink;

    // == 정적 팩토리 메소드 (엔티티 -> DTO 변환) == //
    public static SettlementDetailResponseDto from(Settlement settlement, List<Participant> participantList, OcrReceipt receipt) {
        OcrResultDto ocrResultDto = (receipt != null) ? OcrResultDto.from(receipt) : null;
        String serverBaseUrl = "YOUR_SERVER_ADDRESS"; // TODO: 실제 서버 주소로 변경 필요

        return SettlementDetailResponseDto.builder()
                .id(settlement.getUuid().toString())
                .title(settlement.getTitle())
                .date(settlement.getCreatedAt())
                .status(mapStatusToKorean(settlement.getStatus())) // Enum -> 한글 상태 변환
                .memberCount(settlement.getTotalParticipantCount())
                .receiptImg((receipt != null) ? receipt.getReceiptImageUrl() : null)
                .resultOfOCR(ocrResultDto)
                .participants(participantList.stream()
                        .map(Participant::getParticipantName)
                        .collect(Collectors.toList()))
                .votePageLink(serverBaseUrl + "/vote/" + settlement.getUuid().toString())
                .build();
    }

    // SettlementStatus Enum을 한글 문자열로 변환하는 헬퍼 메소드
    private static String mapStatusToKorean(SettlementStatus status) {
        if (status == null) {
            return "상태 미지정";
        }
        switch (status) {
            case VOTING: return "투표 진행 중";
            case COMPLETED: return "정산 완료";
            default: return "알 수 없음";
        }
    }


    // --- 내부 DTO 클래스들 ---

    @Getter
    @Builder
    public static class OcrResultDto {
        private String storeName;
        private String storeBranch;
        private String bizNum;
        private String address;
        private String tel;
        private String paymentDate;
        private String paymentTime;
        private BigDecimal totalPrice;
        private List<OcrItemDto> items;

        public static OcrResultDto from(OcrReceipt receipt) {
            return OcrResultDto.builder()
                    .storeName(receipt.getStoreName())
                    .storeBranch(receipt.getStoreBranch())
                    .bizNum(receipt.getBizNum())
                    .address(receipt.getAddress())
                    .tel(receipt.getTel())
                    .paymentDate(receipt.getReceiptDate() != null ? receipt.getReceiptDate().toString() : null)
                    .paymentTime(receipt.getPaymentTime() != null ? receipt.getPaymentTime().toString() : null)
                    .totalPrice(receipt.getTotalAmount())
                    .items(receipt.getOcrItems().stream()
                            .map(OcrItemDto::from)
                            .collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @Builder
    public static class OcrItemDto {
        private String name;
        private int count;
        private BigDecimal price;

        public static OcrItemDto from(OcrItem ocrItem) {
            return OcrItemDto.builder()
                    .name(ocrItem.getItemName())
                    .count(ocrItem.getQuantity())
                    .price(ocrItem.getItemPrice())
                    .build();
        }
    }
}