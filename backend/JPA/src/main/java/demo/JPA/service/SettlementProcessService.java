package demo.JPA.service;

import demo.JPA.dto.SettlementCreateRequestDto;
import demo.JPA.entity.Settlement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettlementProcessService {
    
    //DI 추가
    private final SettlementCreationService settlementCreationService;
    private final ParticipantCreationService participantCreationService;



    /**
     * 'POST /settlement' 요청을 처리
     *  1. 일단 settlement 만들고
     *  2-1 OCR 만들고
     *  2-2 Participant 만들고
     *  3. vote 만들기
     *
     *  <db 스키마 외래키 참고하세요>
     */
    @Transactional// controller에서 transaction불가하기에, service로 만듦
    public Settlement createSettlementProcess(SettlementCreateRequestDto requestDto) {

        // 1. 정산(Settlement)을 먼저 생성합니다.
        Settlement settlement = settlementCreationService.createSettlement(requestDto);

        // 2. 참여자(Participant)들을 생성합니다.
        // 이 때 위에서 생성된 settlement 객체를 그대로 사용합니다.
        participantCreationService.createParticipantsForSettlement(
                settlement,
                requestDto.getParticipants()
        );



        // 3. (나중에 구현) OCR 및 Vote 생성 로직...
        // ocrCreationService.createOcr(...)
        
        //4. vote 만들기
        // voteCreationService.createVotes(...)

        // 모든 작업이 성공적으로 끝나면, 트랜잭션이 커밋(Commit)됩니다.
        return settlement;
    }
}