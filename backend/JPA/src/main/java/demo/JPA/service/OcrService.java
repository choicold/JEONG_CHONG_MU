package demo.JPA.service;

import demo.JPA.dto.OcrItemDto;
import demo.JPA.dto.OcrParseResult;
import demo.JPA.entity.OcrItem;
import demo.JPA.entity.OcrReceipt;
import demo.JPA.repository.OcrItemRepository;
import demo.JPA.repository.OcrReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OcrService {

    private final OcrReceiptRepository ocrReceiptRepository;
    private final OcrItemRepository ocrItemRepository;

    /**
     * OCR 결과를 받아서 DB에 저장하는 메서드
     * @param parseResult OCR 결과 DTO
     * @param imageUrl 업로드된 영수증 이미지 URL
     */
    @Transactional
    public void saveOcrResult(OcrParseResult parseResult, String imageUrl) {
        // 1. OCR 영수증 메타 정보 저장
        OcrReceipt receipt = OcrReceipt.builder()
                .receiptImageUrl(imageUrl)
                .totalAmount(parseResult.getTotalAmount())
                .receiptDate(parseResult.getReceiptDate())
                .ocrProcessedAt(OffsetDateTime.now())
                .build();
        ocrReceiptRepository.save(receipt);

        // 2. OCR 항목별 아이템 저장
        List<OcrItem> items = parseResult.getItems().stream()
                .map(dto -> OcrItem.builder()
                        .ocrReceipt(receipt)  // FK 연결
                        .itemName(dto.getItemName())
                        .itemPrice(dto.getItemPrice())
                        .quantity(dto.getQuantity())
                        .build())
                .collect(Collectors.toList());

        ocrItemRepository.saveAll(items);
    }
}
