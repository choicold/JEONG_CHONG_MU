package demo.JPA.repository;

import demo.JPA.entity.OcrItem;
import demo.JPA.entity.OcrReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OcrItemRepository extends JpaRepository<OcrItem, Long> {

    // ✅ 특정 정산(Settlement)에 속한 모든 OCR 아이템 조회
    @Query("SELECT i FROM OcrItem i JOIN i.ocrReceipt r WHERE r.settlement.id = :settlementId")
    List<OcrItem> findAllBySettlementId(@Param("settlementId") Long settlementId);

    // ✅ 특정 영수증에 속한 모든 항목 삭제
    @Modifying
    @Query("DELETE FROM OcrItem i WHERE i.ocrReceipt = :receipt")
    void deleteByReceipt(@Param("receipt") OcrReceipt receipt);
}