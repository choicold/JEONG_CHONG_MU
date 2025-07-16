package demo.JPA.notification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PushTokenRequestDto {
    private String pushToken;
}
