package demo.JPA.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter // JSON으로 변환 시 필드 값을 가져오기 위해 Getter가 필요합니다.
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자를 만듭니다. (new SettlementCreateResponseDto(url))
public class SettlementCreateResponseDto {

    private String settlementUrl; // JSON의 key 이름이 됩니다.
}