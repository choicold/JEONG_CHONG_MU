package demo.JPA.config.security;

import demo.JPA.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class PrincipalDetails implements UserDetails {

    private final Member member;

    public PrincipalDetails(Member member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 모든 사용자에게 "ROLE_USER" 권한 부여
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        // 카카오 로그인이므로 비밀번호는 사용하지 않음
        return null;
    }

    @Override
    public String getUsername() {
        // Spring Security에서 사용자를 식별하는 고유 ID. Member의 ID를 문자열로 변환하여 사용
        return String.valueOf(member.getId());
    }

    // 아래 4개 메서드는 계정 상태에 대한 설정입니다.
    // 특별한 로직이 없다면 모두 true를 반환하도록 설정합니다.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
