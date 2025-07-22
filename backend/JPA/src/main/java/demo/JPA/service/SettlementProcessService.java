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
    public String createSettlementProcess(SettlementCreateRequestDto requestDto) {
        // 1. 정산 생성
        Settlement newSettlement = settlementCreationService.createSettlement(requestDto);

        // 2. OCR 결과 포함 이미지 처리
        for (String url : requestDto.getImageUrl()) {
            OcrReceipt ocrReceipt = ocrReceiptRepository.findByReceiptImageUrl(url)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid image URL: " + url));

            // 2-1. 정산 연결
            ocrReceipt.setSettlement(newSettlement);

            // 2-2. OCR 값 업데이트 (프론트에서 수정된 값 기준)
            if (requestDto.getTotalAmount() != null) {
                ocrReceipt.setTotalAmount(requestDto.getTotalAmount());
            }
            if (requestDto.getReceiptDate() != null) {
                ocrReceipt.setReceiptDate(requestDto.getReceiptDate());
            }
            if (requestDto.getStoreName() != null) {
                ocrReceipt.setStoreName(requestDto.getStoreName());
            }

            // 2-3. OCR 항목도 덮어쓰기
            if (requestDto.getItems() != null) {
                // 기존 항목 제거
                ocrItemRepository.deleteByReceipt(ocrReceipt);

                // 새 항목 추가
                List<OcrItem> items = requestDto.getItems().stream()
                        .map(dto -> OcrItem.builder()
                                .itemName(dto.getItemName())
                                .quantity(dto.getQuantity())
                                .itemPrice(dto.getItemPrice())
                                .build())
                        .collect(Collectors.toList());

                items.forEach(ocrReceipt::addOcrItem);
            }
        }

        // 3. URL 반환
        String settlementUrl = "https://stable-finally-jaybird.ngrok-free.app" + newSettlement.getUuid();
        return settlementUrl;
    }

}