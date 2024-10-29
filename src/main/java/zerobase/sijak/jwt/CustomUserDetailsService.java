package zerobase.sijak.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import zerobase.sijak.persist.domain.Member;
import zerobase.sijak.persist.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String kakaoUserId) throws UsernameNotFoundException {
        return memberRepository
                .findByKakaoUserId(kakaoUserId)
                .map(this::createUserDetails)
                .orElseThrow(
                        () -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다.")
                );
    }

    private UserDetails createUserDetails(Member member) {
        return User.builder()
                .username(member.getKakaoUserId())
                .password(passwordEncoder.encode(member.getProfileNickname()))
                .roles("USER")
                .build();
    }
}
