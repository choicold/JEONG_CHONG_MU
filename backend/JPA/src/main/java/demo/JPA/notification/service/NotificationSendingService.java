package demo.JPA.notification.service;

import com.niamedtech.expo.exposerversdk.ExpoPushNotificationClient;
import com.niamedtech.expo.exposerversdk.request.PushNotification;
import com.niamedtech.expo.exposerversdk.response.TicketResponse;
import demo.JPA.entity.Settlement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSendingService {

    private final StringRedisTemplate redisTemplate;
    private static final String TICKET_KEY_PREFIX = "notification:ticket:";

    public void sendCompletionNotification(Settlement settlement) {
        // 1. 정산 정보에서 hostMember(총무)를 찾아 총무와 연결된 모든 푸시 토큰을 DB에서 가져옴
        List<String> tokens = settlement.getHostMember().getPushTokens().stream()
                .map(token -> token.getTokenValue())
                .toList();

        // 2. 보낼 토큰이 없으면 함수 종료
        if (tokens.isEmpty()) return;

        // 3. 알림에 표시될 제목, 본문과 알림 클릭 시 정산 화면으로 이동하기 위한 정산 UUID를 준비, 이를 바탕으로 실제 전송을 담당하는 sendExpoRequest 호출
        String title = "정산 투표 완료!";
        String message = String.format("'%s' 정산의 모든 참여자가 투표를 완료했습니다. 결과를 확인해주세요.", settlement.getTitle());
        Map<String, Object> data = Map.of("settlementUuid", settlement.getUuid().toString());

        sendExpoRequest(tokens, title, message, data);
    }

    private void sendExpoRequest(List<String> tokens, String title, String message, Map<String, Object> data) {
        List<PushNotification> notifications = new ArrayList<>();
        // 1. Push Token과 알림 메시지 간 1:1 매핑을 위해 각 토큰마다 별도의 메시지를 생성하고 총무가 가진 모든 토큰을 하나씩 순회
        for (String token : tokens) {

            if (token == null || !token.startsWith("ExponentPushToken")) continue;
            // 각 토큰마다 별개의 알림 객체를 생성
            PushNotification pushNotification = new PushNotification();
            // 알림 객체에 알림을 받는 사람, 제목, 본문, UUID를 각각 설정
            pushNotification.setTo(Collections.singletonList(token));
            pushNotification.setTitle(title);
            pushNotification.setBody(message);
            pushNotification.setData(data);
            notifications.add(pushNotification);
        }

        if (notifications.isEmpty()) return;

        // 2. Expo 서버와 통신하기 위한 HTTP 클라이언트를 생성
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ExpoPushNotificationClient client = ExpoPushNotificationClient.builder()
                    .setHttpClient(httpClient)
                    .build();

            /*
             * 3. client.sendPushNotifications(notifications)는 Expo 서버로 알림 목록들을 보내기(라이브러리가 알림 객체 목록들을 JSON형식으로 변환하여 HTTP POST 요청을 보냄)
             * List<TicketResponse.Ticket> response는 Expo 서버가 POST 요청을 받는 즉시 우리 백엔드 서버에 주는 티켓 ID를 저장
             * 티켓 ID로는 추후 우리각 보내고자하는 알림이 올바르게 사용자에게 갔는지 조회할 수 있음(유효시간은 24시간 정도)
             */
            List<TicketResponse.Ticket> response = client.sendPushNotifications(notifications);

            // 4. 받은 티켓ID를 하나씩 확인
            for (int i = 0; i < response.size(); i++) {
                TicketResponse.Ticket ticket = response.get(i);
                String originalToken = notifications.get(i).getTo().get(0);

                // 접수 상태가 OK라면 티켓ID 발급 성공!(알림이 올바르게 갔는지는 아직 모름)
                if ("OK".equalsIgnoreCase(ticket.getStatus().toString())) {
                    String redisKey = TICKET_KEY_PREFIX + ticket.getId();
                    // 발급 받은 티켓ID를 나중에 올바르게 사용자에게 전송되었는지 확인하기 위해 Redis에 24시간 동안 임시 저장
                    redisTemplate.opsForValue().set(redisKey, originalToken, 24, TimeUnit.HOURS);
                } else {
                    log.error("티켓 발급 실패: {}, 상세: {}", ticket.getMessage(), ticket.getDetails());
                }
            }
        } catch (Exception e) {
            log.error("알림 전송 중 예외 발생", e);
        }
    }
}
