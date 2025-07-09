package demo.JPA.repository;

import demo.JPA.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    // 참여자 ID로 모든 투표 내역을 찾는 쿼리 메소드
    List<Vote> findByParticipantId(Long participantId);

    // 항목 ID로 모든 투표 내역을 찾는 쿼리 메소드
    List<Vote> findByOcrItemId(Long ocrItemId);
}