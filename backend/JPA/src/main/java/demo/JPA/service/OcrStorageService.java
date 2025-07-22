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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OcrStorageService {

    private final OcrReceiptRepository ocrReceiptRepository;
    private final OcrItemRepository ocrItemRepository;

    /**
     * 사용자가 수정한 OCR 결과를 DB에 저장(업데이트 또는 생성)하는 메서드
     * @param modifiedResult 사용자가 수정한 내용이 담긴 DTO
     * @return 저장되거나 업데이트된 OcrReceipt 엔티티
     */
    public OcrReceipt saveOcrResult(OcrParseResult modifiedResult) {

        // ✅ 1. 이미지 URL을 기준으로 기존 영수증 데이터가 있는지 찾아봅니다.
        OcrReceipt receipt = ocrReceiptRepository.findByReceiptImageUrl(modifiedResult.getImageUrl())
                .orElseGet(() -> OcrReceipt.builder() // 📌 없으면 새로 만들 준비를 합니다.
                        .receiptImageUrl(modifiedResult.getImageUrl())
                        .ocrProcessedAt(OffsetDateTime.now())
                        .build());

        // ✅ 2. DTO의 내용으로 영수증 필드를 전부 업데이트합니다.
        //      (새로 만든 영수증이든, 기존 영수증이든 모두 최신 정보로 덮어씁니다)
        receipt.setStoreName(modifiedResult.getStoreName());
        receipt.setStoreBranch(modifiedResult.getStoreBranch());
        receipt.setBizNum(modifiedResult.getBizNum());
        receipt.setAddress(modifiedResult.getAddress());
        receipt.setTel(modifiedResult.getTel());
        receipt.setReceiptDate(modifiedResult.getReceiptDate());
        receipt.setPaymentTime(modifiedResult.getPaymentTime());
        receipt.setTotalAmount(modifiedResult.getTotalAmount());
        // receipt.setTotalSource(modifiedResult.getTotalSource()); // 필요시 주석 해제

        // ✅ 3. 영수증에 딸린 '항목(Item)'들도 업데이트합니다.
        //      기존 항목들을 모두 지우고, DTO에 있는 새 항목들로 교체하는 방식이 가장 간단하고 안전합니다.
        receipt.getItems().clear(); // orphanRemoval=true 옵션 덕분에 여기서 기존 자식 엔티티가 DB에서 삭제됩니다.

        if (modifiedResult.getItems() != null) {
            List<OcrItem> newItems = modifiedResult.getItems().stream()
                    .map(itemDto -> OcrItem.builder()
                            .itemName(itemDto.getName()) // DTO의 name -> Entity의 itemName
                            .itemPrice(itemDto.getPrice()) // DTO의 price -> Entity의 itemPrice
                            .quantity(itemDto.getCount()) // DTO의 count -> Entity의 quantity
                            .build())
                    .collect(Collectors.toList());

            newItems.forEach(receipt::addOcrItem); // 새로 만든 아이템들을 영수증에 추가합니다.
        }

        // ✅ 4. 최종적으로 영수증을 저장합니다.
        //      JPA가 알아서 새 영수증이면 INSERT, 기존 영수증이면 UPDATE 쿼리를 날려줍니다.
        return ocrReceiptRepository.save(receipt);
    }
}