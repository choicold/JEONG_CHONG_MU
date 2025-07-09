package demo.JPA.service;

import demo.JPA.dto.SettlementCreateRequestDto;
import demo.JPA.entity.Member;
import demo.JPA.entity.Settlement;
import demo.JPA.repository.MemberRepository;
import demo.JPA.repository.OcrItemRepository;
import demo.JPA.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service // 이 클래스가 서비스 계층의 컴포넌트임을 Spring에 알립니다.
@RequiredArgsConstructor // final로 선언된 필드들의 생성자 DI를 수행합니다.
public class SettlementCreationService {

    // final 키워드를 사용하여 DI 받은 객체가 변경되지 않도록 보장합니다.
    private final SettlementRepository settlementRepository;
    private final MemberRepository memberRepository; // 주최자 정보를 가져오기 위해 필요

    private final OcrItemRepository ocrItemRepository;



    /**
     * host_member_id 존재하는지 확인
     * settlement 객체 생성
     * 저장하기
     **/
    @Transactional // 이 메서드의 모든 DB 작업은 하나의 트랜잭션으로 처리됩니다.
    public Settlement createSettlement(SettlementCreateRequestDto requestDto) {

        // 1. DTO에서 hostMemberId를 가져와 Member 엔티티를 조회합니다.
        Member host = memberRepository.findById(requestDto.getHostMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid host member ID"));

        // 2. DTO와 조회한 Member 엔티티를 사용하여 새로운 Settlement 엔티티를 생성합니다.
        // (Settlement 엔티티에 생성자 또는 빌더가 정의되어 있어야 합니다)
        Settlement newSettlement = Settlement.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .hostMember(host)
                //.deadline(requestDto.getDeadline())
                .build();

        // 3. settlementRepository를 통해 DB에 저장하고, 저장된 객체를 반환합니다.
        return settlementRepository.save(newSettlement);
    }
}