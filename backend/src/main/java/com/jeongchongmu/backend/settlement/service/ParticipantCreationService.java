package com.jeongchongmu.backend.settlement.service;

import com.jeongchongmu.backend.settlement.dto.ParticipantCreateDto;
import com.jeongchongmu.backend.member.entity.Member;
import com.jeongchongmu.backend.settlement.entity.Participant;
import com.jeongchongmu.backend.settlement.entity.Settlement;
import com.jeongchongmu.backend.member.repository.MemberRepository;
import com.jeongchongmu.backend.settlement.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 정산 참여자(Participant) 생성을 담당하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class ParticipantCreationService {

    private final ParticipantRepository participantRepository;
    private final MemberRepository memberRepository;

    /**
     * 특정 정산(Settlement)에 속한 참여자(Participant)들을 생성합니다.
     * @param settlement 참여자들이 속할 부모 정산 엔티티
     * @param participantDtos 생성할 참여자들의 정보가 담긴 DTO 리스트
     */
    @Transactional
    public void createParticipantsForSettlement(Settlement settlement, List<ParticipantCreateDto> participantDtos)
            throws IllegalArgumentException{
        // 참여자 정보가 없으면 예러 발생
        if (participantDtos == null || participantDtos.isEmpty()) {
            throw new IllegalArgumentException("participantDtos is null or empty");
        }

        // Settlement의 호스트(생성자) 정보를 가져옵니다.
        Member hostMember = settlement.getHostMember();

        // 1. DTO 리스트를 Participant 엔티티 리스트로 변환합니다.
        List<Participant> participants = participantDtos.stream()
                .map(dto -> {
                    // 각 참여자를 위한 고유 토큰을 생성합니다.
                    String uniqueToken = UUID.randomUUID().toString();

                    // 빌더 패턴을 사용하여 Participant 엔티티를 생성합니다.
                    return Participant.builder()
                            .settlement(settlement) // 부모 정산 엔티티 관계 설정 (JPA가 settlement_id를 관리)
                            .member(hostMember)     // 정산 생성자(Host) 관계 설정 (JPA가 member_id를 관리)
                            .participantUuid(dto.getUuid())
                            .participantName(dto.getName())
                            .participantThumbnailUrl(dto.getThumbnailUrl())
                            .uniqueLinkToken(uniqueToken) // 고유 접근 토큰 설정
                            .build();
                })
                .collect(Collectors.toList());

        // 2. 변환된 모든 Participant 엔티티 리스트를 DB에 한 번에 저장 (Bulk Insert)
        participantRepository.saveAll(participants);




        /* ---------participant에 기존 회원의 유무를 판단해서 집어넣음
                    정총무 어플 첫 페이지에, 돈 받아야하는 정산내역들 + 설문해야하는 정산내역들
                    보이고 싶을때 사용
        // 1. DTO 리스트에서 kakao_id(DTO에서는 id 필드)만 추출하여 새로운 리스트를 만듭니다.
        //    null일 수 있는 id는 필터링합니다.
        List<Long> kakaoIds = participantDtos.stream()
                .map(ParticipantCreateDto::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 2. 추출된 kakao_id 리스트를 사용하여, 해당하는 Member들을 단 한 번의 DB 쿼리로 모두 조회합니다.
        //    그리고 빠른 조회를 위해 kakao_id를 키로 하는 Map으로 변환합니다.
        Map<Long, Member> memberMapByKakaoId = memberRepository.findAllByKakaoIdIn(kakaoIds).stream()
                .collect(Collectors.toMap(Member::getKakaoId, member -> member));

        // ------------------------------------

        // 3. 각 DTO를 Participant 엔티티로 변환합니다.
        List<Participant> participants = participantDtos.stream()
                .map(dto -> {
                    // Map에서 kakao_id로 Member를 찾습니다. 이 과정에서는 DB 조회가 발생하지 않습니다.
                    Member member = memberMapByKakaoId.get(dto.getId());

                    String uniqueToken = UUID.randomUUID().toString();

                    // 빌더를 사용해 Participant 엔티티를 생성합니다.
                    return Participant.builder()
                            .settlement(settlement) // 부모 정산 엔티티와 관계를 맺어줍니다.
                            .participantName(dto.getName())
                            .participantThumbnailUrl(dto.getThumbnailUrl())
                            .participantUuid(dto.getUuid())
                            .member(member) // 앱 회원인 경우 Member 엔티티를, 비회원이면 null을 설정합니다.
                            .uniqueLinkToken(uniqueToken)
                            .build();
                })
                .collect(Collectors.toList());

        // 4. 변환된 모든 Participant 엔티티 리스트를 DB에 한 번에 저장(bulk insert)합니다.
        participantRepository.saveAll(participants);

         */
    }
}
