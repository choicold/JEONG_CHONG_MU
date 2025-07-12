package com.jeongchongmu.backend.ocr.service;

import com.jeongchongmu.backend.ocr.dto.OcrParseResult;
import com.jeongchongmu.backend.ocr.entity.OcrItem;
import com.jeongchongmu.backend.ocr.entity.OcrReceipt;
import com.jeongchongmu.backend.ocr.repository.OcrReceiptRepository;
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
        // 📌 [수정] 중복된 코드 한 줄 삭제 및 settlement 관련 로직 제거
        OcrReceipt receipt = OcrReceipt.builder()
                .receiptImageUrl(imageUrl)
                .totalAmount(parseResult.getTotalAmount())
                .receiptDate(parseResult.getReceiptDate())
                .ocrProcessedAt(OffsetDateTime.now())
                .build();

        List<OcrItem> items = parseResult.getItems().stream()
                .map(dto -> OcrItem.builder()
                        .itemName(dto.getItemName())
                        .itemPrice(dto.getItemPrice())
                        .quantity(dto.getQuantity())
                        .build())
                .collect(Collectors.toList());

        items.forEach(receipt::addOcrItem);

        return ocrReceiptRepository.save(receipt);
    }
}
