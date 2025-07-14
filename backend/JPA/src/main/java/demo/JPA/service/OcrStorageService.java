package demo.JPA.service;

import demo.JPA.dto.OcrParseResult;
import demo.JPA.entity.OcrItem;
import demo.JPA.entity.OcrReceipt;
import demo.JPA.entity.Settlement;
import demo.JPA.repository.OcrReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OcrStorageService {

    private final OcrReceiptRepository ocrReceiptRepository;

    // 📌 [수정] settlementId 파라미터 제거
    @Transactional
    public OcrReceipt saveOcrResult(OcrParseResult parseResult, String imageUrl) {
        OcrReceipt receipt = OcrReceipt.builder()
                .receiptImageUrl(imageUrl)
                .totalAmount(parseResult.getTotalAmount())
                .receiptDate(parseResult.getReceiptDate())
                .ocrProcessedAt(OffsetDateTime.now())
                .storeName(parseResult.getStoreName())
                .storeBranch(parseResult.getStoreBranch())
                .bizNum(parseResult.getBizNum())
                .address(parseResult.getAddress())
                .tel(parseResult.getTel())
                .paymentTime(parseResult.getPaymentTime())
                .build();

        List<OcrItem> items = parseResult.getItems().stream()
                .map(dto -> OcrItem.builder()
                        .itemName(dto.getItemName())
                        .itemPrice(dto.getItemPrice())
                        .quantity(dto.getQuantity())
                        .build())
                .collect(Collectors.toList());

        items.forEach(receipt::addOcrItem); // ✨ 이 메서드가 올바르게 구현되었는지 2번 항목에서 확인

        return ocrReceiptRepository.save(receipt);
    }
}