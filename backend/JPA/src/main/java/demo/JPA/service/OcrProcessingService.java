package demo.JPA.service;

import demo.JPA.dto.OcrParseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class OcrProcessingService {

    private final S3UploadService s3UploadService;
    private final ClovaOcrService clovaOcrService;
    private final OcrStorageService ocrStorageService;


    @Transactional
    public String processReceipt(MultipartFile file) throws IOException {

        //1. 파일을 S3에 업로드하고 이미지 URL을 받아옴
        String imageUrl = s3UploadService.upload(file, "receipts");

        // 2. Clova OCR API 호출하여 결과 파싱
        OcrParseResult parseResult = clovaOcrService.callClovaOcr(file);



        // 3. 파싱된 결과를 DB에 저장
        ocrStorageService.saveOcrResult(parseResult, imageUrl); // 요청대로 imageUrl을 전달

        //4. 리턴
        return imageUrl;
    }
}