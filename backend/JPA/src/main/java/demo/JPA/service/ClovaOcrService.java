package demo.JPA.service;

import demo.JPA.dto.OcrItemDto;
import demo.JPA.dto.OcrParseResult;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Clova OCR 호출 및 결과 파싱 담당 서비스
 */
@Service
@RequiredArgsConstructor
public class ClovaOcrService {

    @Value("${clova.api.url}")
    private String apiUrl;

    @Value("${clova.api.secret}")
    private String secretKey;

    @Value("${server.imageBaseUrl}")
    private String imageBaseUrl; // 예: https://yourdomain.com/picture/

    /**
     * 이미지 URL을 기반으로 Clova OCR을 호출하고, 결과를 파싱
     * @param imageFilename 저장된 이미지 파일 이름 (예: abc123.jpg)
     * @return 파싱된 OCR 결과 DTO
     */
    public OcrParseResult callClovaOcr(String imageFilename) {
        try {
            // Clova 요청 JSON 구성
            JSONObject requestBody = new JSONObject();
            requestBody.put("version", "V2");
            requestBody.put("requestId", UUID.randomUUID().toString());
            requestBody.put("timestamp", System.currentTimeMillis());

            JSONArray images = new JSONArray();
            JSONObject image = new JSONObject();
            image.put("format", "jpg");
            image.put("url", imageBaseUrl + imageFilename);
            image.put("name", "receipt");

            images.put(image);
            requestBody.put("images", images);

            // HTTP 연결
            URL url = new URL(apiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-OCR-SECRET", secretKey);
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setDoOutput(true);

            // 요청 전송
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 응답 수신
            StringBuilder response = new StringBuilder();
            try (var br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(con.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            // OCR 결과 파싱
            JSONObject json = new JSONObject(response.toString());

            JSONObject receipt = json.getJSONArray("images")
                    .getJSONObject(0)
                    .getJSONObject("receipt")
                    .getJSONObject("result");

            JSONArray itemsArray = receipt.getJSONArray("subResults")
                    .getJSONObject(0)
                    .getJSONArray("items");

            // 항목명, 수량, 가격
            List<OcrItemDto> itemDtos = new ArrayList<>();
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject item = itemsArray.getJSONObject(i);
                String name = item.getJSONObject("name").getString("text");
                int quantity = Integer.parseInt(item.getJSONObject("count").getString("text"));
                int price = Integer.parseInt(item.getJSONObject("price").getJSONObject("price").getString("text"));

                itemDtos.add(new OcrItemDto(name, quantity, (double) price));
            }

            // 총액
            double totalPrice = Double.parseDouble(receipt
                    .getJSONObject("totalPrice")
                    .getJSONObject("price")
                    .getString("text")
                    .replace(",", ""));

            // 날짜
            LocalDate date = LocalDate.parse(receipt
                    .getJSONObject("paymentInfo")
                    .getJSONObject("date")
                    .getString("text"));

            // 이미지 URL 포함한 파싱 결과 반환
            return OcrParseResult.builder()
                    .imageUrl(imageBaseUrl + imageFilename)
                    .receiptDate(date)
                    .totalAmount(totalPrice)
                    .items(itemDtos)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Clova OCR 호출 실패", e);
        }
    }
}
