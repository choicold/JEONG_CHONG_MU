package com.jeongchongmu.backend.vote.dto;

public record VoteLoadItemDto(
        Long itemId, //OCR_item의 키
        String itemName,//아이템 이름
        Double price,// 가격
        int quantity,// 개수
        Boolean myVote // 나의 투표 상태 (true: 참여, false: 미참여)
) {}
