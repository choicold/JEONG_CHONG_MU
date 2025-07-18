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

    /**
     * OCR 항목들(OcrItem)을 정산(Settlement)과 연결하는 메서드
     *
     * @param receipt    OCR 결과가 저장된 영수증 객체
     * @param settlement 연결할 정산 객체
     */
    @Transactional
    public void attachOcrItemsToSettlement(OcrReceipt receipt, Settlement settlement) {
        if (receipt.getItems() == null || receipt.getItems().isEmpty()) return;

        for (OcrItem item : receipt.getItems()) {
            item.setSettlement(settlement); // JPA 변경 감지로 DB 업데이트됨
        }
    }


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