package demo.JPA.service;

import demo.JPA.dto.SettlementResultDto;
import demo.JPA.entity.Member;
import demo.JPA.entity.OcrItem;
import demo.JPA.entity.Settlement;
import demo.JPA.entity.Vote;
import demo.JPA.repository.OcrItemRepository;
import demo.JPA.repository.SettlementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 최종 정산 금액 계산을 전담하는 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SettlementResultService {

    private final SettlementRepository settlementRepository;
    private final OcrItemRepository ocrItemRepository;

    public SettlementResultDto calculateAndGetResult(UUID settlementUuid) {
        Settlement settlement = settlementRepository.findByUuid(settlementUuid)
                .orElseThrow(() -> new EntityNotFoundException("정산을 찾을 수 없습니다."));

        // 1. 해당 정산의 모든 항목(OcrItem)을 가져옵니다.
        List<OcrItem> items = ocrItemRepository.findAllBySettlementId(settlement.getId());
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 2. 각 참여자가 내야 할 총 금액을 저장할 Map을 생성합니다.
        Map<String, BigDecimal> amountPerParticipant = new HashMap<>();

        // 3. 각 항목(item)별로 정산 시작
        for (OcrItem item : items) {
            totalAmount = totalAmount.add(item.getItemPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

            // 4. 이 항목에 참여(isParticipated=true)한 투표(vote)만 필터링합니다.
            List<Vote> votesForThisItem = item.getVotes().stream()
                    .filter(vote -> vote.getIsParticipated() != null && vote.getIsParticipated())
                    .toList();

            if (votesForThisItem.isEmpty()) continue; // 아무도 참여 안했으면 스킵

            // 5. 항목 금액 / 참여자 수 = 1인당 부담금 (소수점 없이 반올림)
            BigDecimal amountPerPerson = item.getItemPrice()
                    .divide(BigDecimal.valueOf(votesForThisItem.size()), 0, RoundingMode.HALF_UP);

            // 6. 각 참여자의 총 부담금에 더하기
            for (Vote vote : votesForThisItem) {
                String name = vote.getParticipant().getParticipantName();
                amountPerParticipant.merge(name, amountPerPerson, BigDecimal::add);
            }
        }

        // 7. 총무의 계좌 정보를 가져오기
        Member host = settlement.getHostMember();

        // 8. 최종 DTO 리스트 생성
        List<SettlementResultDto.ParticipantResult> participantResults = amountPerParticipant.entrySet().stream()
                .map(entry -> {
                    String name = entry.getKey();
                    BigDecimal amount = entry.getValue();
                    String tossLink = createTossLink(host.getBankName(), host.getAccountNumber(), amount);
                    return SettlementResultDto.ParticipantResult.builder()
                            .participantName(name)
                            .amountToPay(amount)
                            .tossPayLink(tossLink)
                            .build();
                })
                .collect(Collectors.toList());

        return SettlementResultDto.builder()
                .settlementTitle(settlement.getTitle())
                .hostName(host.getNickname())
                .totalAmount(totalAmount)
                .participantResults(participantResults)
                .build();
    }

    // 토스 딥링크를 생성
    private String createTossLink(String bankName, String accountNumber, BigDecimal amount) {
        if (bankName == null || accountNumber == null || amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return null; // 계좌 정보나 금액이 없으면 링크 생성 불가
        }
        try {
            // 은행 이름은 URL 인코딩을 해주는 것이 안전하다네요.
            String encodedBankName = URLEncoder.encode(bankName, StandardCharsets.UTF_8);
            return String.format("supertoss://send?amount=%d&bank=%s&accountNo=%s",
                    amount.intValue(), encodedBankName, accountNumber);
        } catch (Exception e) {
            // 로깅 추가
            return null;
        }
    }
}
