package demo.JPA.dto;

import lombok.AccessLevel;
import lombok.Builder; // Builder μ„ν¬νΈ
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class OcrParseResult {

    @Setter
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
    @Builder // π‘‡ [μμ•] μ΄ ν΄λμ¤μ— @Builder μ–΄λ…Έν…μ΄μ…μ„ μ¶”κ°€ν•©λ‹λ‹¤.
    public static class OcrItemDto {
        private String name;
        private int count;
        private BigDecimal price;
    }

}