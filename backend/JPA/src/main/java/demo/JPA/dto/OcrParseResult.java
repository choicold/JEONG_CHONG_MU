package demo.JPA.dto;

import lombok.Builder; // Builder 임포트
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class OcrParseResult {
    private String imageUrl;
    private String storeName;
    private String storeBranch;
    private String bizNum;
    private String address;
    private String tel;
    private LocalDate receiptDate;
    private LocalTime paymentTime;
    private BigDecimal totalAmount;
    private List<OcrItemDto> items;
    private TotalSourceType totalSource;

    @Getter
    @Builder // 👇 [수정] 이 클래스에 @Builder 어노테이션을 추가합니다.
    public static class OcrItemDto {
        private String itemName;
        private int quantity;
        private BigDecimal itemPrice;
    }
}