package demo.JPA.service;

import demo.JPA.dto.OcrParseResult;
import demo.JPA.entity.OcrReceipt;
import demo.JPA.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OcrProcessingService {

//    private final S3UploadService s3UploadService;
    private final ClovaOcrService clovaOcrService;
    private final OcrStorageService ocrStorageService;
    private final SettlementRepository settlementRepository;


    @Transactional
    public OcrReceipt processReceipt(MultipartFile file) {

        // 1. 파일을 S3에 업로드하고 이미지 URL을 받아옴
//        String imageUrl = s3UploadService.upload(file, "receipts");

//        String imageUrl = "https://jeong-chong-mu-s3.s3.ap-northeast-2.amazonaws.com/receipt_image.png";

        // 2. Clova OCR API 호출하여 결과 파싱
        OcrParseResult parseResult = clovaOcrService.callClovaOcr(file);

        // 3. 파싱된 결과를 DB에 저장
        return ocrStorageService.saveOcrResult(parseResult, file.getOriginalFilename());

        //이미지 url를 사용자에게 전송해야함.
    }
}