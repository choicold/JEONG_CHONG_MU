package demo.JPA.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberAccountUpdateRequestDto {
    private String bankName;
    private String accountNumber;
}
