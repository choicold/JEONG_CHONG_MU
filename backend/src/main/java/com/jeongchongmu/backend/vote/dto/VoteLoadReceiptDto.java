package com.jeongchongmu.backend.vote.dto;

import java.time.LocalDate;
import java.util.List;

public record VoteLoadReceiptDto(
        Long receiptId,//영수증 id
        LocalDate receiptDate,//영수증 날짜
        List<VoteLoadItemDto> items // 영수증 항목들
) {}
