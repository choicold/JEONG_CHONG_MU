package com.jeongchongmu.backend.member.service;

import com.jeongchongmu.backend.auth.dto.KakaoUserResponse;
import com.jeongchongmu.backend.auth.service.KakaoUserService;
import com.jeongchongmu.backend.member.entity.Member;
import com.jeongchongmu.backend.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final KakaoUserService kakaoUserService;

    public MemberService(MemberRepository memberRepository, KakaoUserService kakaoUserService) {
        this.memberRepository = memberRepository;
        this.kakaoUserService = kakaoUserService;
    }

    // 카카오 로그인 처리
    public Member processKakaoLogin(String accessToken) {
        // 1. 카카오 API로부터 사용자 정보 조회
        KakaoUserResponse kakaoUser = kakaoUserService.getUserInfo(accessToken);
        Long kakaoId = Long.parseLong(kakaoUser.id());

        // 2. 기존 회원 여부 확인
        Optional<Member> existingMember = memberRepository.findByKakaoId(kakaoId);

        if (existingMember.isPresent()) {
            // 3-1. 기존 회원인 경우 : 정보 업데이트 후 반환
            Member member = existingMember.get();
            updateMemberInfo(member, kakaoUser);
            return memberRepository.save(member);
        } else {
            // 3-2. 신규 회원인 경우 : 회원 등록
            Member newMember = Member.fromKakaoUSer(kakaoUser);
            return memberRepository.save(newMember);
        }
    }

    // 기존 회원 정보 업데이트
    private void updateMemberInfo(Member member, KakaoUserResponse kakaoUser) {
        if (kakaoUser.kakaoAccount() != null) {
            var kakaoAccount = kakaoUser.kakaoAccount();

            if (kakaoAccount.profile() != null) {
                var profile = kakaoAccount.profile();
                member.setNickname(profile.nickname());
                member.setProfileImageUrl(profile.profileImageUrl());
                member.setThumbnailImageUrl(profile.thumbnailImageUrl());
                member.setIsDefaultImage(profile.isDefaultImage());
                member.setIsDefaultNickname(profile.isDefaultNickname());
            }
        }
    }

    // 카카오 ID로 회원 조회
    @Transactional(readOnly = true)
    public Optional<Member> findByKakaoId(Long kakaoId) {
        return memberRepository.findByKakaoId(kakaoId);
    }

    // 회원 ID로 조회
    @Transactional(readOnly = true)
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }
}