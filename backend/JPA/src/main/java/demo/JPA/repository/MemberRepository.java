package demo.JPA.repository;

import demo.JPA.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // Kakao ID로 회원을 찾는 쿼리 메소드
    Optional<Member> findByKakaoId(Long kakaoId);
    Optional<Member> findById(Long id);

    List<Member> findAllByKakaoIdIn(List<Long> kakaoIds);

}