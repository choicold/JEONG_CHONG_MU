package demo.JPA.service;

import demo.JPA.dto.ClovaOcrResponseDto;
import demo.JPA.dto.OcrParseResult;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClovaOcrService {

    // 📌 WebClient.Builder를 주입받도록 변경
    private final WebClient.Builder webClientBuilder;

    @Value("${clova.api.url}")
    private String apiUrl;

    @Value("${clova.api.secret}")
    private String secretKey;

    // 📌 파라미터로 MultipartFile을 직접 받도록 변경
    public OcrParseResult callClovaOcr(MultipartFile file) {
        try {
            // 1. Clova OCR에 보낼 'message' JSON 파트 생성
            JSONObject message = new JSONObject()
                    .put("version", "V2")
                    .put("requestId", UUID.randomUUID().toString())
                    .put("timestamp", System.currentTimeMillis())
                    .put("images", new org.json.JSONArray().put(new JSONObject()
                            .put("name", "receipt")
                            .put("format", "jpg"))); // 또는 png

            // 2. multipart/form-data 요청 본문 생성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("message", message.toString());
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });

            // 3. WebClient를 사용하여 파일과 함께 API 호출
            ClovaOcrResponseDto responseDto = webClientBuilder.build().post()
                    .uri(apiUrl)
                    .header("X-OCR-SECRET", secretKey)
                    .contentType(MediaType.MULTIPART_FORM_DATA) // 📌 Content-Type 변경
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(ClovaOcrResponseDto.class) // 응답은 DTO로 자동 변환
                    .block(); // 동기식으로 결과를 기다림

            if (responseDto == null) {
                throw new RuntimeException("Clova OCR API 호출에 실패했습니다.");
            }

            // 4. 응답 결과를 OcrParseResult로 변환
            return convertToParseResult(responseDto);

        } catch (IOException e) {
            throw new RuntimeException("파일을 읽는 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            throw new RuntimeException("Clova OCR 호출 실패", e);
        }
    }

    private OcrParseResult convertToParseResult(ClovaOcrResponseDto responseDto) {
        ClovaOcrResponseDto.Image image = responseDto.getImages().stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("OCR 결과에 이미지가 없습니다."));

        ClovaOcrResponseDto.ReceiptResult result = image.getReceipt().getResult();

        List<OcrParseResult.OcrItemDto> items = Optional.ofNullable(result.getSubResults())
                .orElse(Collections.emptyList())
                .stream()
                .findFirst()
                .map(ClovaOcrResponseDto.SubResult::getItems)
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> {
                    // 📌 [수정] 각 필드가 null일 경우를 대비하여 Optional로 안전하게 처리합니다.
                    String name = Optional.ofNullable(item.getName())
                            .map(ClovaOcrResponseDto.FormattedText::getFormattedText)
                            .orElse(""); // 이름이 없으면 빈 문자열

                    int quantity = Optional.ofNullable(item.getCount())
                            .map(count -> count.getFormattedAsInt(1))
                            .orElse(1); // 수량이 없으면 기본값 1

                    BigDecimal price = Optional.ofNullable(item.getPrice())
                            .map(ClovaOcrResponseDto.PriceInfo::getAsBigDecimal)
                            .orElse(BigDecimal.ZERO); // 가격이 없으면 0

                    return new OcrParseResult.OcrItemDto(name, quantity, price);
                })
                .collect(Collectors.toList());

        BigDecimal totalPrice = Optional.ofNullable(result.getTotalPrice())
                .map(ClovaOcrResponseDto.PriceInfo::getAsBigDecimal)
                .filter(price -> price.compareTo(BigDecimal.ZERO) > 0)
                .orElseGet(() -> items.stream()
                        .map(item -> item.getItemPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        return OcrParseResult.builder()
                .imageUrl(null)
                .receiptDate(result.getPaymentInfo().getDate().getFormattedDate())
                .totalAmount(totalPrice)
                .items(items)
                .build();
    }
}