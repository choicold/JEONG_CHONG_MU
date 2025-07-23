package demo.JPA.service;

import demo.JPA.dto.SettlementCreateRequestDto;
import demo.JPA.entity.Member;
import demo.JPA.entity.OcrItem;
import demo.JPA.entity.OcrReceipt;
import demo.JPA.entity.Settlement;
import demo.JPA.repository.MemberRepository;
import demo.JPA.repository.OcrItemRepository;
import demo.JPA.repository.OcrReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettlementProcessService {

    private final SettlementCreationService settlementCreationService;
    private final OcrReceiptRepository ocrReceiptRepository; // 👇 [추가] DI 추가
    private final OcrItemRepository ocrItemRepository;

    @Transactional
    public String createSettlementProcess(SettlementCreateRequestDto requestDto, Long hostMemberId) {
        // 1. 정산(Settlement) 엔티티 생성
        Settlement newSettlement = settlementCreationService.createSettlement(requestDto, hostMemberId);

        // 2. ✨ [로직 변경] DTO 정보를 바탕으로 영수증(OcrReceipt) 및 항목(OcrItem) 엔티티를 '새로' 생성
        // 현재는 영수증이 1개인 경우만 상정하고 구현합니다.
        if (requestDto.getImageUrl() != null && !requestDto.getImageUrl().isEmpty()) {

            // 2-1. OcrReceipt 엔티티 생성
            OcrReceipt newOcrReceipt = OcrReceipt.builder()
                    .receiptImageUrl(requestDto.getImageUrl().get(0)) // 첫번째 이미지 URL 사용
                    .storeName(requestDto.getStoreName())
                    .storeBranch(requestDto.getStoreBranch())
                    .bizNum(requestDto.getBizNum())
                    .address(requestDto.getAddress())
                    .tel(requestDto.getTel())
                    .receiptDate(requestDto.getReceiptDate())
                    .paymentTime(requestDto.getPaymentTime())
                    .totalAmount(requestDto.getTotalAmount())
                    .build();

            // 2-2. OcrItem 엔티티 리스트 생성
            if (requestDto.getItems() != null) {
                List<OcrItem> newOcrItems = requestDto.getItems().stream()
                        .map(itemDto -> OcrItem.builder()
                                .itemName(itemDto.getItemName())
                                .quantity(itemDto.getQuantity())
                                .itemPrice(itemDto.getItemPrice())
                                .build())
                        .collect(Collectors.toList());
                // 생성된 아이템들을 영수증에 추가
                newOcrItems.forEach(newOcrReceipt::addOcrItem);
            }

            // 2-3. 생성된 영수증을 정산에 추가 (JPA Cascade 설정에 의해 함께 저장됨)
            newSettlement.getOcrReceipts().add(newOcrReceipt);
            newOcrReceipt.setSettlement(newSettlement); // 양방향 연관관계 설정
        }

        // 3. URL 반환
        // settlementRepository.save(newSettlement)를 명시적으로 호출할 필요가 없습니다.
        // 트랜잭션이 끝날 때 변경된 newSettlement가 자동으로 DB에 반영(dirty checking)됩니다.
        String settlementUrl = "https://stable-finally-jaybird.ngrok-free.app/vote/" + newSettlement.getUuid();
        return settlementUrl;
    }

}