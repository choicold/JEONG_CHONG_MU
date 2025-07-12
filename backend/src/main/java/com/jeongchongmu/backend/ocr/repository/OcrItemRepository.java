package com.jeongchongmu.backend.ocr.repository;

import com.jeongchongmu.backend.ocr.entity.OcrItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OcrItemRepository extends JpaRepository<OcrItem, Long> {
    // 특정 정산(Settlement)에 속한 모든 OCR 아이템을 조회합니다.
    @Query("SELECT i FROM OcrItem i JOIN i.ocrReceipt r WHERE r.settlement.id = :settlementId")
    List<OcrItem> findAllBySettlementId(@Param("settlementId") Long settlementId);

}
