package com.jeongchongmu.backend.settlement.repository;

import com.jeongchongmu.backend.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    // 주최자(host member)의 ID로 모든 정산을 찾는 쿼리 메소드
    List<Settlement> findByHostMemberId(Long hostMemberId);
}
