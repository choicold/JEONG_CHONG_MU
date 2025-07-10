package demo.JPA.repository;

import demo.JPA.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    // ▼▼▼▼▼ 기존 메소드를 아래 @Query 메소드로 대체하거나 새로 만드세요 ▼▼▼▼▼
    /**
     * 특정 참여자의 모든 Vote 정보를 가져올 때,
     * 연관된 OcrItem과 OcrReceipt를 함께 '즉시 로딩(Eager Loading)'합니다.
     * N+1 문제를 해결하기 위한 Fetch Join 구문입니다.
     */
    @Query("SELECT v FROM Vote v " +
            "JOIN FETCH v.ocrItem i " +
            "JOIN FETCH i.ocrReceipt " +
            "WHERE v.participant.id = :participantId")
    List<Vote> findByParticipantIdWithDetails(@Param("participantId") Long participantId);

    Optional<Vote> findByParticipantIdAndOcrItemId(Long participantId, Long ocrItemId);
}