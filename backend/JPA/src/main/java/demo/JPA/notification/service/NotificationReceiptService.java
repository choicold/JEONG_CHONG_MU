package demo.JPA.notification.service;

import com.niamedtech.expo.exposerversdk.ExpoPushNotificationClient;
import com.niamedtech.expo.exposerversdk.response.ReceiptResponse;
import demo.JPA.notification.repository.MemberPushTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NotificationReceiptService {

    private final StringRedisTemplate redisTemplate;
    private final MemberPushTokenRepository memberPushTokenRepository;

    // 여기에서의 영수증은 알림 메시지가 APNS나 FCM 서버에 성공적으로 전달되었는지를 알려주는 것을 의미
    @Scheduled(cron = "0 */15 * * * *") // 이 메서드를 15분 주기로 자동으로 실행시킴, Redis에 저장된 티켓 ID를 Expo 서버에 보내 잘 전송됐는지 여부를 확인하고 삭제
    public void checkReceiptsAndCleanUp() {
        // 1. Redis에서 처리할 티켓ID 목록을 가져옴
        Set<String> ticketKeys = redisTemplate.keys("notification:ticket:*");
        if (ticketKeys == null || ticketKeys.isEmpty()) {
            return;
        }

        List<String> ticketIds = new ArrayList<>();
        for (String key : ticketKeys) {
            ticketIds.add(key.replace("notification:ticket:", ""));
        }

        log.info("Checking {} push notification receipts from Redis.", ticketIds.size());

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ExpoPushNotificationClient client = ExpoPushNotificationClient.builder()
                    .setHttpClient(httpClient)
                    .build();

            // 2. 라이브러리의 메소드를 호출하여 영수증 요청
            Map<String, ReceiptResponse.Receipt> receipts = client.getPushNotificationReceipts(ticketIds);

            // 3. 받은 영수증을 하나씩 처리
            for (Map.Entry<String, ReceiptResponse.Receipt> entry : receipts.entrySet()) {
                String ticketId = entry.getKey();
                ReceiptResponse.Receipt receipt = entry.getValue();

                if ("error".equalsIgnoreCase(receipt.getStatus().toString())) {
                    // 4. DeviceNotRegistered 오류인 경우, 해당 토큰을 DB에서 삭제(유령 토큰이므로)
                    if ("DeviceNotRegistered".equals(receipt.getDetails().getError())) {
                        String redisKey = "notification:ticket:" + ticketId;
                        String tokenValueToDelete = redisTemplate.opsForValue().get(redisKey);
                        if (tokenValueToDelete != null) {
                            log.info("Deleting stale push token: {}", tokenValueToDelete);
                            memberPushTokenRepository.deleteByTokenValue(tokenValueToDelete);
                        }
                    } else {
                        log.error("Push notification failed for ticket {}: {}", ticketId, receipt.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error while checking push receipts", e);
        } finally {
            // 5. 처리가 끝난 티켓들은 더 이상 확인할 필요가 없으므로 Redis에서 모두 삭제
            redisTemplate.delete(ticketKeys);
        }
    }
}
