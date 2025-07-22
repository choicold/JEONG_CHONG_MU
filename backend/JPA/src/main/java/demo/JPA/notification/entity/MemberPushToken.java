package demo.JPA.notification.entity;

import demo.JPA.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_push_token")
public class MemberPushToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "token_value", unique = true, nullable = false)
    private String tokenValue;

    public MemberPushToken(Member member, String tokenValue) {
        this.member = member;
        this.tokenValue = tokenValue;
    }
}
