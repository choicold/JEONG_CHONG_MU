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
    public String createSettlementProcess(SettlementCreateRequestDto requestDto, Member host) {
        // 1. 정산(Settlement) 엔티티 생성
        Settlement newSettlement = settlementCreationService.createSettlement(requestDto, host);

        // 2. OcrData 객체를 먼저 가져오기
        SettlementCreateRequestDto.OcrDataDto ocrData = requestDto.getOcrData();

        // 3. ocrData 객체가 null이 아닌지, 그 안의 imageUrl이 유효한지 확인
        if (ocrData != null && ocrData.getImageUrl() != null && !ocrData.getImageUrl().isEmpty()) {

            // 3-1. OcrReceipt 엔티티 생성 (ocrData 객체에서 필드를 가져오기)
            OcrReceipt newOcrReceipt = OcrReceipt.builder()
                    .receiptImageUrl(ocrData.getImageUrl())
                    .storeName(ocrData.getStoreName())
                    .storeBranch(ocrData.getStoreBranch())
                    .bizNum(ocrData.getBizNum())
                    .address(ocrData.getAddress())
                    .tel(ocrData.getTel())
                    .receiptDate(ocrData.getReceiptDate())
                    .paymentTime(ocrData.getPaymentTime())
                    .totalAmount(ocrData.getTotalAmount())
                    .build();

            // 3-2. OcrItem 엔티티 리스트 생성 (ocrData 객체에서 items를 가져오기)
            if (ocrData.getItems() != null) {
                List<OcrItem> newOcrItems = ocrData.getItems().stream()
                        .map(itemDto -> OcrItem.builder()
                                .itemName(itemDto.getItemName())
                                .quantity(itemDto.getQuantity())
                                .itemPrice(itemDto.getItemPrice())
                                .build())
                        .collect(Collectors.toList());
                // 생성된 아이템들을 영수증에 추가
                newOcrItems.forEach(newOcrReceipt::addOcrItem);
            }

            // 3-3. 생성된 영수증을 정산에 추가
            newSettlement.getOcrReceipts().add(newOcrReceipt);
            newOcrReceipt.setSettlement(newSettlement);
        }

        // 4. URL 반환
        // settlementRepository.save(newSettlement)를 명시적으로 호출할 필요가 없습니다.
        // 트랜잭션이 끝날 때 변경된 newSettlement가 자동으로 DB에 반영(dirty checking)됩니다.
        String settlementUrl = "https://stable-finally-jaybird.ngrok-free.app/vote/" + newSettlement.getUuid();
        return settlementUrl;
    }

}