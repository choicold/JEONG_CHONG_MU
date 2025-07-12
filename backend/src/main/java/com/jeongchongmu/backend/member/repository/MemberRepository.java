package com.jeongchongmu.backend.member.repository;

import com.jeongchongmu.backend.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 카카오 ID로 회원 조회
    Optional<Member> findByKakaoId(Long kakaoId);

    // 카카오 ID 존재 여부 확인
    boolean existsByKakaoId(Long kakaoId);
}
