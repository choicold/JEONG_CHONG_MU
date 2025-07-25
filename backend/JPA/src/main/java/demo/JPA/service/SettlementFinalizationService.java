package demo.JPA.service;

import demo.JPA.entity.Settlement;
import demo.JPA.entity.SettlementStatus;
import demo.JPA.repository.SettlementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementFinalizationService {

    private final SettlementRepository settlementRepository;

    @Value("${domain.url}")
    private String URL;

    @Transactional
    public String finalizeAndGenerateLink(UUID settlementUuid) {
        Settlement settlement = settlementRepository.findByUuid(settlementUuid)
                .orElseThrow(() -> new EntityNotFoundException("정산을 찾을 수 없습니다."));

        if (settlement.getStatus() != SettlementStatus.COMPLETED) {
            throw new IllegalStateException("투표가 완료된 정산만 최종 확정할 수 있습니다.");
        }

        settlement.setStatus(SettlementStatus.FINALIZED);

        return URL + "/final-result.html?uuid=" + settlement.getUuid();
    }
}
