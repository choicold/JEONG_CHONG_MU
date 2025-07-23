package demo.JPA.controller;

import demo.JPA.dto.OcrParseResult;
import demo.JPA.service.OcrProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
public class OcrController {

    private final OcrProcessingService ocrProcessingService;

    // (2) OCR 분석 결과 반환 (이미지 URL을 받아서 결과 제공)
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeReceipt(@RequestPart("file") MultipartFile file) {
        try {
            OcrParseResult result = ocrProcessingService.processReceiptAndReturnResult(file);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("파일 처리 오류: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("OCR 처리 중 오류 발생: " + e.getMessage());
        }
    }
}
