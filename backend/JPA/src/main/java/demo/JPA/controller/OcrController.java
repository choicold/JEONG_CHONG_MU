package demo.JPA.controller;

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

    /**
     * 사진 여러개받아서, 이미지 링크 리턴
     * @param files
     *  Key: file, Value: 사진1.jpg
     *  Key: file, Value: 사진2.png
     *  Key: file, Value: 사진3.jpg
     *
     * @return
     * {
     *   "imageUrls": [
     *     "https://.../receipts/uuid-주소1.jpg",
     *     "https://.../receipts/uuid-주소2.png",
     *     "https://.../receipts/uuid-주소3.gif"
     *   ]
     * }
     */
    @PostMapping("/process")
    public ResponseEntity<?> processReceipt(@RequestPart("file") List<MultipartFile> files) {
        // 파일이 비어있는지 확인
        if (files == null || files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)) {
            return ResponseEntity.badRequest().body("업로드할 파일을 1개 이상 선택해주세요.");
        }

        // 2. 여러 개의 이미지 URL을 담을 리스트를 생성합니다.
        List<String> imageUrls = new ArrayList<>();

        try {
            // 3. 반복문을 통해 각 파일을 하나씩 처리합니다.
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String imageUrl = ocrProcessingService.processReceipt(file);
                    imageUrls.add(imageUrl); // 결과를 리스트에 추가
                }
            }

            // 4. 모든 이미지 URL이 담긴 리스트를 JSON 형식으로 반환합니다.
            return ResponseEntity.ok(Map.of("imageUrls", imageUrls));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("파일 처리 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("영수증 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}