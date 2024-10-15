package zerobase.sijak.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import zerobase.sijak.persist.repository.KakaoRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final KakaoRepository kakaoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return kakaoRepository
                .findByEmail(email)
                .map(this::createUserDetails)
                .orElseThrow(
                        () -> new UsernameNotFoundException("해당 유저가 존재하지 않습니다.")
                );
    }

    private UserDetails createUserDetails(KakaoUser kakaouser) {
        return User.builder()
                .username(kakaouser.getEmail())
                .password(passwordEncoder.encode(kakaouser.getNickname()))
                .roles("USER")
                .build();
    }
}
