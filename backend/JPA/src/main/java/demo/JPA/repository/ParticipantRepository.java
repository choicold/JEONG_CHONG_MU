package demo.JPA.repository;

import demo.JPA.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {


    long countBySettlementId(Long settlementId);

    List<Participant> findBySettlementId(Long settlementId);

    // ✨ [추가] 특정 정산(settlementId) 내에서 이름으로 참여자 찾기
    Optional<Participant> findBySettlementIdAndParticipantName(Long settlementId, String participantName);

}