package demo.JPA.service;

import demo.JPA.dto.SettlementCreateRequestDto;
import demo.JPA.entity.Member;
import demo.JPA.entity.OcrReceipt;
import demo.JPA.entity.Settlement;
import demo.JPA.repository.MemberRepository;
import demo.JPA.repository.OcrReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettlementProcessService {

    private final SettlementCreationService settlementCreationService;
    private final OcrReceiptRepository ocrReceiptRepository; // 👇 [추가] DI 추가

    @Transactional
    public String createSettlementProcess(SettlementCreateRequestDto requestDto) {

        // 1. 정산(Settlement)을 먼저 생성합니다.
        // createSettlement의 반환값을 Settlement 객체로 받습니다.
        Settlement newSettlement = settlementCreationService.createSettlement(requestDto);

        // 2. DTO에 포함된 이미지 URL들을 이용해 OcrReceipt를 찾아 Settlement와 연결합니다.
        for (String url : requestDto.getImageUrl()) {
            // 이미지 URL로 해당 OcrReceipt를 찾습니다.
            OcrReceipt ocrReceipt = ocrReceiptRepository.findByReceiptImageUrl(url)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid image URL: " + url));

            // OcrReceipt에 방금 생성한 Settlement를 연결해줍니다. (JPA가 변경 감지하여 UPDATE)
            ocrReceipt.setSettlement(newSettlement);
        }

        // 3. 생성된 정산의 고유 URL을 만들어 반환합니다. (프론트엔드 주소에 맞게 수정 필요)
        String settlementUrl = "http://your-frontend-domain/vote/" + newSettlement.getUuid();

        return settlementUrl;
    }
}
//OCR 정보가 DTO에 들어오면 그 값들을 ocrReceipt 값 업데이트 랑 ocrItem 추가
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

        // 2-2. OCR 값이 DTO에 포함되어 있다면 업데이트
        if (requestDto.getTotalAmount() != null) {
            ocrReceipt.setTotalPrice(requestDto.getTotalAmount());
        }
        if (requestDto.getReceiptDate() != null) {
            ocrReceipt.setDate(requestDto.getReceiptDate());
        }

        // ⚠️ OCR 항목들(OcrItem)은 따로 저장해야 함 → OcrItemService.createFromDto(...) 등 따로 구현 필요
        // 예시: ocrItemService.saveItems(ocrReceipt, requestDto.getItems());
    }

    // 3. URL 반환
    String settlementUrl = "http://your-frontend-domain/vote/" + newSettlement.getUuid();
    return settlementUrl;
}