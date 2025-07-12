package demo.JPA.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
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
        private PriceInfo totalPrice;
        private PaymentInfo paymentInfo;
        private List<SubResult> subResults;
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
    public static class PaymentInfo {
        private FormattedDate date;
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
                return Integer.parseInt(getFormattedText());
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
                return null;
            }
        }
    }

    @Getter @Setter
    public static class FormattedValue {
        private String value;
    }
}