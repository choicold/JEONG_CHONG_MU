package com.jeongchongmu.backend.ocr.repository;

import com.jeongchongmu.backend.ocr.entity.OcrReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OcrReceiptRepository extends JpaRepository<OcrReceipt, Long> {
}