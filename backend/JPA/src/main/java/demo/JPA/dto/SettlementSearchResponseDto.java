package demo.JPA.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 정산 검색창 구현에 필요한 기초 데이터를 담는 DTO
 */
@Getter
@Builder
public class SettlementSearchResponseDto {

    // 정산 UUID
    private UUID settlementUuid;

    // 정산 제목
    private String title;

    // 정산 생성 날짜
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private OffsetDateTime createdDate;

    // 가게 이름 (대표 영수증 1개의 가게 이름)
    private String storeName;
}