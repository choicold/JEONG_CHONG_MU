package com.jeongchongmu.backend.ocr.controller;

import com.jeongchongmu.backend.ocr.entity.OcrReceipt;
import com.jeongchongmu.backend.ocr.service.OcrProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
public class OcrController {

    private final OcrProcessingService ocrProcessingService;

    /**
     * 영수증 이미지와 정산 ID를 함께 받아 모든 처리를 수행하는 통합 API
     * @param file 영수증 이미지 파일
     * @return 저장된 영수증 정보 (JSON)
     */
    @PostMapping("/process")
    public ResponseEntity<OcrReceipt> processReceipt(@RequestPart("file") MultipartFile file){
        // 📌 [수정] 이제 OcrProcessingService의 processReceipt는 file을 정상적으로 받을 수 있습니다.
        OcrReceipt savedReceipt = ocrProcessingService.processReceipt(file);
        return ResponseEntity.ok(savedReceipt);
    }
}
