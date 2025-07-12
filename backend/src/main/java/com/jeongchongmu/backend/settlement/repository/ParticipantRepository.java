package com.jeongchongmu.backend.settlement.repository;

import com.jeongchongmu.backend.settlement.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    // 특정 정산(settlement)에 속한 모든 참여자를 찾는 쿼리 메소드
    List<Participant> findBySettlementId(Long settlementId);

    @Query("SELECT p FROM Participant p JOIN FETCH p.settlement WHERE p.uniqueLinkToken = :token")
    Optional<Participant> findByUniqueLinkTokenWithSettlement(@Param("token") String uniqueLinkToken);

    Optional<Participant> findByUniqueLinkToken(String uniqueLinkToken);
}
