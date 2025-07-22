package demo.JPA.service;

import demo.JPA.dto.ClovaOcrResponseDto;
import demo.JPA.dto.OcrParseResult;
import demo.JPA.dto.TotalSourceType;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClovaOcrService {

    private final WebClient.Builder webClientBuilder;

    @Value("${clova.api.url}")
    private String apiUrl;

    @Value("${clova.api.secret}")
    private String secretKey;

    public OcrParseResult callClovaOcr(MultipartFile file) {
        try {
            JSONObject message = new JSONObject()
                    .put("version", "V2")
                    .put("requestId", UUID.randomUUID().toString())
                    .put("timestamp", System.currentTimeMillis())
                    .put("images", new org.json.JSONArray().put(new JSONObject()
                            .put("name", "receipt")
                            .put("format", "jpg")));

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("message", message.toString());
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });

            ClovaOcrResponseDto responseDto = webClientBuilder.build().post()
                    .uri(apiUrl)
                    .header("X-OCR-SECRET", secretKey)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(ClovaOcrResponseDto.class)
                    .block();

            if (responseDto == null) {
                throw new RuntimeException("Clova OCR API 호출에 실패했습니다.");
            }
            return convertToParseResult(responseDto);
        } catch (IOException e) {
            throw new RuntimeException("파일을 읽는 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            throw new RuntimeException("Clova OCR 호출 중 실패: " + e.getMessage(), e);
        }
    }


    private OcrParseResult convertToParseResult(ClovaOcrResponseDto responseDto) {
        ClovaOcrResponseDto.Image image = responseDto.getImages().stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("OCR 결과에 이미지가 없습니다."));

        if (image.getReceipt() == null || image.getReceipt().getResult() == null) {
            throw new IllegalArgumentException("유효한 영수증 결과가 없습니다.");
        }

        ClovaOcrResponseDto.ReceiptResult result = image.getReceipt().getResult();
        ClovaOcrResponseDto.StoreInfo storeInfo = result.getStoreInfo();
        ClovaOcrResponseDto.PaymentInfo paymentInfo = result.getPaymentInfo();

        // 항목(items) 파싱
        List<OcrParseResult.OcrItemDto> items = Optional.ofNullable(result.getSubResults())
                .orElse(Collections.emptyList())
                .stream()
                .findFirst()
                .map(ClovaOcrResponseDto.SubResult::getItems)
                .orElse(Collections.emptyList())
                .stream()
                .map(item -> {
                    String name = Optional.ofNullable(item.getName())
                            .map(ClovaOcrResponseDto.FormattedText::getFormattedText)
                            .orElse("");
                    int quantity = Optional.ofNullable(item.getCount())
                            .map(count -> count.getFormattedAsInt(1))
                            .orElse(1);
                    BigDecimal price = Optional.ofNullable(item.getPrice())
                            .map(ClovaOcrResponseDto.PriceInfo::getAsBigDecimal)
                            .orElse(BigDecimal.ZERO);
                    return OcrParseResult.OcrItemDto.builder()
                            .name(name).count(quantity).price(price)
                            .build();
                })
                .collect(Collectors.toList());

        // 총액(totalPrice) 파싱
        BigDecimal clovaTotal = Optional.ofNullable(result.getTotalPrice())
                .map(ClovaOcrResponseDto.PriceInfo::getAsBigDecimal)
                .orElse(null);

        BigDecimal itemSum = items.stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal finalTotal;
        TotalSourceType totalSource;

        if (clovaTotal == null || clovaTotal.compareTo(BigDecimal.ZERO) <= 0) {
            finalTotal = itemSum;
            totalSource = TotalSourceType.ITEM_SUM;
        } else if (itemSum.compareTo(BigDecimal.ZERO) <= 0) {
            finalTotal = clovaTotal;
            totalSource = TotalSourceType.CLOVA_TOTAL;
        } else {
            BigDecimal diff = clovaTotal.subtract(itemSum).abs();
            BigDecimal ratio = diff.divide(clovaTotal, 2, RoundingMode.HALF_UP);

            if (ratio.compareTo(new BigDecimal("0.30")) > 0) {
                finalTotal = itemSum;
                totalSource = TotalSourceType.ITEM_SUM;
            } else {
                finalTotal = clovaTotal;
                totalSource = TotalSourceType.CLOVA_TOTAL;
            }
        }

        // 상세 정보 파싱
        String storeName = Optional.ofNullable(storeInfo.getName()).map(ClovaOcrResponseDto.FormattedText::getFormattedText).orElse(null);
        String storeBranch = Optional.ofNullable(storeInfo.getSubName()).map(ClovaOcrResponseDto.FormattedText::getFormattedText).orElse(null);
        String bizNum = Optional.ofNullable(storeInfo.getBizNum()).map(ClovaOcrResponseDto.FormattedText::getFormattedText).orElse(null);
        String address = Optional.ofNullable(storeInfo.getAddresses()).flatMap(list -> list.stream().findFirst()).map(ClovaOcrResponseDto.FormattedText::getFormattedText).orElse(null);
        String tel = Optional.ofNullable(storeInfo.getTel()).flatMap(list -> list.stream().findFirst()).map(ClovaOcrResponseDto.FormattedText::getFormattedText).orElse(null);

        // 날짜 파싱
        LocalDate receiptDate = Optional.ofNullable(paymentInfo.getDate()).map(ClovaOcrResponseDto.FormattedDate::getFormattedDate).orElse(null);

        // 시간 파싱
        LocalTime paymentTime = Optional.ofNullable(paymentInfo)
                .map(ClovaOcrResponseDto.PaymentInfo::getTime)
                .map(ClovaOcrResponseDto.FormattedTime::getText)
                .map(text -> {
                    try {
                        return LocalTime.parse(text.replaceAll("\\s", ""));
                    } catch (DateTimeParseException e) {
                        return null; // 파싱 실패 시 null 반환
                    }
                })
                .orElse(null);

        // 최종 DTO 빌드
        return OcrParseResult.builder()
                .storeName(storeName)
                .storeBranch(storeBranch)
                .bizNum(bizNum)
                .address(address)
                .tel(tel)
                .receiptDate(receiptDate)
                .paymentTime(paymentTime)
                .totalAmount(finalTotal)        // 👈 보정된 total 변수
                .totalSource(totalSource)
                .items(items)
                .build();
    }
}