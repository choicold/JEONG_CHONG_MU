package demo.JPA.repository;

import demo.JPA.entity.OcrReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OcrReceiptRepository extends JpaRepository<OcrReceipt, Long> {
    Optional<OcrReceipt> findByReceiptImageUrl(String receiptImageUrl);

    @Query("SELECT r FROM OcrReceipt r JOIN FETCH r.ocrItems WHERE r.settlement.id = :settlementId")
    List<OcrReceipt> findAllWithItemsBySettlementId(@Param("settlementId") Long settlementId);

    Optional<OcrReceipt> findFirstBySettlementIdOrderByCreatedAtDesc(Long settlementId);
}