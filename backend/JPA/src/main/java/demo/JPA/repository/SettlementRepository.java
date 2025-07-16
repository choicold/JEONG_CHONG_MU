package demo.JPA.repository;

import demo.JPA.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    // 주최자(host member)의 ID로 모든 정산을 찾는 쿼리 메소드
    List<Settlement> findByHostMemberId(Long hostMemberId);

    Optional<Settlement> findByUuid(UUID uuid);

    /**
     * ✨ [추가] 특정 멤버가 관련된 모든 정산 목록을 조회하는 쿼리
     * 멤버가 정산의 '호스트'이거나, '참여자' 목록에 포함된 경우를 모두 찾습니다.
     * DISTINCT를 사용하여 중복된 결과는 제거합니다.
     * @param memberId 멤버의 내부 ID (PK)
     * @param participantName 멤버의 닉네임 (Participant 테이블과 조인하기 위함)
     * @return 해당 멤버의 정산 목록
     */
    @Query("SELECT DISTINCT s FROM Settlement s " +
            "LEFT JOIN s.participants p " +
            "WHERE s.hostMember.id = :memberId OR p.participantName = :participantName " +
            "ORDER BY s.createdAt DESC")
    List<Settlement> findSettlementsByMember(@Param("memberId") Long memberId, @Param("participantName") String participantName);
}