package demo.JPA.repository;

import demo.JPA.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    // 특정 정산(settlement)에 속한 모든 참여자를 찾는 쿼리 메소드
    List<Participant> findBySettlementId(Long settlementId);
}