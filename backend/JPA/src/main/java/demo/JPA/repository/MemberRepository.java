package demo.JPA.repository;

import demo.JPA.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Kakao ID로 회원을 조회
    Optional<Member> findByKakaoId(Long kakaoId);

    // UUID로 회원을 조회
    Optional<Member> findByUuid(UUID uuid);
}