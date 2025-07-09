package demo.JPA.repository;

import demo.JPA.entity.OcrReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OcrReceiptRepository extends JpaRepository<OcrReceipt, Long> {
}