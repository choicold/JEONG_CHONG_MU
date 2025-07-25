package demo.JPA.notification.repository;

import demo.JPA.entity.Member;
import demo.JPA.notification.entity.MemberPushToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberPushTokenRepository extends JpaRepository<MemberPushToken, Long> {
    List<MemberPushToken> findAllByMember(Member member);
    Optional<MemberPushToken> findByTokenValue(String tokenValue);
    void deleteByMemberIdAndTokenValue(Long memberId, String tokenValue);
    void deleteByTokenValue(String tokenValue);
    void deleteAllByMemberId(Long memberId);
}
