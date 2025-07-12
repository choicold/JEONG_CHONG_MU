package com.jeongchongmu.backend.global_controller;

import com.jeongchongmu.backend.member.entity.Member;
import com.jeongchongmu.backend.member.repository.MemberRepository;
import com.jeongchongmu.backend.ocr.entity.OcrItem;
import com.jeongchongmu.backend.ocr.entity.OcrReceipt;
import com.jeongchongmu.backend.ocr.repository.OcrItemRepository;
import com.jeongchongmu.backend.ocr.repository.OcrReceiptRepository;
import com.jeongchongmu.backend.settlement.entity.Participant;
import com.jeongchongmu.backend.settlement.entity.Settlement;
import com.jeongchongmu.backend.settlement.repository.ParticipantRepository;
import com.jeongchongmu.backend.settlement.repository.SettlementRepository;
import com.jeongchongmu.backend.vote.entity.Vote;
import com.jeongchongmu.backend.vote.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // @Controller + @ResponseBody, JSON/XML 등 데이터를 반환하는 컨트롤러
@RequestMapping("/data_api") // 이 컨트롤러의 모든 메소드는 /data_api 주소로 시작
@RequiredArgsConstructor //롬복(Lombok) 라이브러리에서 제공하는 어노테이션으로, 클래스의 final 필드나 @NonNull이 붙은 필드에 대해 생성자를 자동으로 생성해 줍니다.
public class DataApiController {

    private final MemberRepository memberRepository;
    private final SettlementRepository settlementRepository;
    private final OcrReceiptRepository ocrReceiptRepository;
    private final OcrItemRepository ocrItemRepository;
    private final ParticipantRepository participantRepository;
    private final VoteRepository voteRepository;

    @GetMapping("/members")
    public List<Member> getMembers() {
        return memberRepository.findAll();
    }

    @GetMapping("/settlements")
    public List<Settlement> getSettlements() {
        return settlementRepository.findAll();
    }

    @GetMapping("/ocr-receipts")
    public List<OcrReceipt> getOcrReceipts() {
        return ocrReceiptRepository.findAll();
    }

    @GetMapping("/ocr-items")
    public List<OcrItem> getOcrItems() {
        return ocrItemRepository.findAll();
    }

    @GetMapping("/participants")
    public List<Participant> getParticipants() {
        return participantRepository.findAll();
    }

    @GetMapping("/votes")
    public List<Vote> getVotes() {
        return voteRepository.findAll();
    }
}