package demo.JPA.service;

import demo.JPA.dto.OcrParseResult;
import demo.JPA.entity.OcrItem;
import demo.JPA.entity.OcrReceipt;
import demo.JPA.repository.OcrItemRepository;
import demo.JPA.repository.OcrReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OcrStorageService {

    private final OcrReceiptRepository ocrReceiptRepository;
    private final OcrItemRepository ocrItemRepository;  // ✅ 누락된 부분

    public OcrReceipt saveOcrResult(OcrParseResult parseResult, String imageUrl) {

        Optional<OcrReceipt> existingReceiptOpt = ocrReceiptRepository.findByReceiptImageUrl(imageUrl);

        existingReceiptOpt.ifPresent(existingReceipt -> {
            // ✅ 기존 OCR 아이템 삭제
            ocrItemRepository.deleteByReceipt(existingReceipt);
            // ✅ 기존 OCR 영수증 삭제
            ocrReceiptRepository.delete(existingReceipt);
        });

        // 1. 새로운 OcrReceipt 생성
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
                .totalSource(parseResult.getTotalSource())  // 총액 출처 구분 (텍스트)
                .build();

        // 2. 항목들을 엔티티로 변환 및 연결
        List<OcrItem> items = parseResult.getItems().stream()
                .map(dto -> OcrItem.builder()
                        .itemName(dto.getItemName())
                        .itemPrice(dto.getItemPrice())
                        .quantity(dto.getQuantity())
                        .build())
                .collect(Collectors.toList());

        items.forEach(receipt::addOcrItem);

        // 3. 저장
        return ocrReceiptRepository.save(receipt);
    }
}
