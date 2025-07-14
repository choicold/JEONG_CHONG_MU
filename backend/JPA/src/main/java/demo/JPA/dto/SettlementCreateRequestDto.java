package demo.JPA.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class SettlementCreateRequestDto {

    // Settlement 정보
    private String title;
    private Long hostMemberId;

    private Integer participantsNum;

    private List<String> imageUrl;
}