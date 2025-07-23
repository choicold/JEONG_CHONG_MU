package demo.JPA.service;

import demo.JPA.dto.SettlementCreateRequestDto;
import demo.JPA.entity.Member;
import demo.JPA.entity.Settlement;
import demo.JPA.repository.MemberRepository;
import demo.JPA.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettlementCreationService {

    private final SettlementRepository settlementRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Settlement createSettlement(SettlementCreateRequestDto requestDto, Member host) {
        Settlement newSettlement = Settlement.builder()
                .title(requestDto.getTitle())
                .hostMember(host)
                .totalParticipantCount(requestDto.getParticipantsNum())
                .build();

        return settlementRepository.save(newSettlement);
    }
}