package demo.JPA.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime; // LocalTime 임포트 추가
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

// API 응답의 모든 필드를 매핑할 필요 없으므로, 모르는 필드는 무시하도록 설정
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class ClovaOcrResponseDto {

    private List<Image> images;

    @Getter @Setter
    public static class Image {
        private Receipt receipt;
    }

    @Getter @Setter
    public static class Receipt {
        private ReceiptResult result;
    }

    @Getter @Setter
    public static class ReceiptResult {
        private StoreInfo storeInfo; // [추가] 상점 정보
        private PriceInfo totalPrice;
        private PaymentInfo paymentInfo;
        private List<SubResult> subResults;
    }

    // ▼▼▼ [추가] 상점 정보를 담을 내부 클래스 ▼▼▼
    @Getter @Setter
    public static class StoreInfo {
        private FormattedText name;
        private FormattedText subName; // 지점명
        private FormattedText bizNum;  // 사업자번호
        private List<FormattedText> addresses;
        private List<FormattedText> tel;
    }

    @Getter @Setter
    public static class PaymentInfo {
        private FormattedDate date;
        private FormattedTime time; // [추가] 시간 정보
    }

    @Getter @Setter
    public static class SubResult {
        private List<Item> items;
    }

    @Getter @Setter
    public static class Item {
        private FormattedText name;
        private FormattedText count;
        private PriceInfo price;
    }

    @Getter @Setter
    public static class PriceInfo {
        private PriceDetail price;

        public BigDecimal getAsBigDecimal() {
            return Optional.ofNullable(price).map(PriceDetail::getAsBigDecimal).orElse(BigDecimal.ZERO);
        }
    }

    @Getter @Setter
    public static class PriceDetail {
        private String text;
        private FormattedValue formatted;

        public BigDecimal getAsBigDecimal() {
            String value = (formatted != null && formatted.getValue() != null) ? formatted.getValue() : text;
            if (value == null) return BigDecimal.ZERO;
            // 숫자, 점(.)을 제외한 모든 문자(예: 원, 콤마)를 제거
            return new BigDecimal(value.replaceAll("[^0-9.]", ""));
        }
    }

    @Getter @Setter
    public static class FormattedText {
        private String text;

        public String getFormattedText() {
            return Optional.ofNullable(text).map(String::trim).orElse("");
        }

        public int getFormattedAsInt(int defaultValue) {
            try {
                // 숫자 외의 문자를 모두 제거하고 파싱
                String cleanedText = getFormattedText().replaceAll("\\D", "");
                if (cleanedText.isEmpty()) return defaultValue;
                return Integer.parseInt(cleanedText);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
    }

    @Getter @Setter
    public static class FormattedDate {
        private String text;

        public LocalDate getFormattedDate() {
            try {
                return Optional.ofNullable(text).map(LocalDate::parse).orElse(null);
            } catch (DateTimeParseException e) {
                // yyyy-MM-dd 외 다른 형식도 파싱 시도 (필요 시)
                return null;
            }
        }
    }

    // ▼▼▼ [추가] 시간 정보를 파싱할 내부 클래스 ▼▼▼
    @Getter @Setter
    public static class FormattedTime {
        private String text;

        public LocalTime getFormattedTime() {
            try {
                return Optional.ofNullable(text).map(LocalTime::parse).orElse(null);
            } catch (DateTimeParseException e) {
                return null;
            }
        }
    }

    @Getter @Setter
    public static class FormattedValue {
        private String value;
    }
}