package demo.JPA.service;

import demo.JPA.dto.OcrParseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class OcrProcessingService {

    private final S3UploadService s3UploadService;
    private final ClovaOcrService clovaOcrService;

    public OcrParseResult processReceiptAndReturnResult(MultipartFile file) throws IOException {

        // 1. S3 업로드
        String imageUrl = s3UploadService.upload(file, "receipts");

        // 2. OCR 호출 및 결과 파싱
        OcrParseResult parseResult = clovaOcrService.callClovaOcr(file);

        // 3. imageUrl 세팅 (parseResult에 저장되지 않았다면 수동 세팅 필요)
        parseResult.setImageUrl(imageUrl);

        // 4. DB 저장은 하지 않고 결과만 반환
        return parseResult;
    }

}