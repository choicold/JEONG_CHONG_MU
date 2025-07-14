package demo.JPA.repository;

import demo.JPA.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {


    long countBySettlementId(Long settlementId);

    List<Participant> findBySettlementId(Long settlementId);

}