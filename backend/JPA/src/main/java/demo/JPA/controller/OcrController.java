package demo.JPA.controller;

import demo.JPA.dto.ImageRequestDto;
import demo.JPA.dto.OcrCorrectionRequestDto;
import demo.JPA.dto.OcrParseResult;
import demo.JPA.service.OcrProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
public class OcrController {

    private final OcrProcessingService ocrProcessingService;

    // (1) 이미지 업로드하고 URL만 반환
    @PostMapping("/process")
    public ResponseEntity<?> processReceipt(@RequestPart("file") List<MultipartFile> files) {
        if (files == null || files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)) {
            return ResponseEntity.badRequest().body("업로드할 파일을 1개 이상 선택해주세요.");
        }

        List<String> imageUrls = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String imageUrl = ocrProcessingService.processReceipt(file);
                    imageUrls.add(imageUrl);
                }
            }

            return ResponseEntity.ok(Map.of("imageUrls", imageUrls));

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("파일 처리 중 오류: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("OCR 처리 중 오류: " + e.getMessage());
        }
    }

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

    // (3) 수정된 OCR 결과 저장
    @PostMapping("/correct")
    public ResponseEntity<Void> correctOcr(@RequestBody OcrCorrectionRequestDto dto) {
        ocrProcessingService.correctOcrResult(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveModifiedOcr(@RequestBody OcrParseResult modifiedResult) {
        try {
            ocrStorageService.saveOcrResult(modifiedResult, modifiedResult.getImageUrl());
            return ResponseEntity.ok("OCR 결과 저장 성공");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("저장 중 오류 발생: " + e.getMessage());
        }
    }
}
