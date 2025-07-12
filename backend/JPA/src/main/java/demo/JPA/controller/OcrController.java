package demo.JPA.controller;

import demo.JPA.dto.OcrParseResult;
import demo.JPA.entity.OcrReceipt;
import demo.JPA.service.ClovaOcrService;  // Clova OCR API 호출을 담당
import demo.JPA.service.OcrService;       // 파싱된 결과를 DB에 저장
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ocr")  // OCR 관련 API는 모두 이 경로 아래로 통일
@RequiredArgsConstructor
public class OcrController {

    private final ClovaOcrService clovaOcrService; // 실제 OCR 호출
    private final OcrService ocrService;           // DB 저장

    /**
     * OCR 이미지 업로드 및 처리 API
     * @param file 영수증 이미지 파일
     * @param settlementId 어떤 정산 참여 세션에 해당하는 영수증인지
     * @return 저장된 영수증 정보 (JSON)
     */
    @PostMapping("/upload")
    public ResponseEntity<OcrReceipt> uploadReceipt(
            @RequestPart("file") MultipartFile file,
            @RequestParam("settlementId") Long settlementId) {

        // 1. Clova OCR API 호출 → OCR 파싱 결과 + 이미지 URL 반환
        OcrParseResult parseResult = clovaOcrService.callClovaOcr(file);

        // 2. OCR 결과 저장 (영수증 + 항목들)
        OcrReceipt savedReceipt = ocrService.saveOcrResult(
                parseResult,
                parseResult.getImageUrl(),  // 이미지 업로드 URL
                settlementId
        );

        // 3. 저장된 영수증 반환
        return ResponseEntity.ok(savedReceipt);
    }
}
