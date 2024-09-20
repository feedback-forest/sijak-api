package zerobase.sijak.jwt;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import zerobase.sijak.dto.SocialType;
import zerobase.sijak.dto.kakao.KakaoUserInfo;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long kakaoUserId;

    private String nickname;

    private String profileImageUrl;

    private String name;

    private String email;

    private String gender;

    private String ageRange;

    private String appliedClass;

    private String latitude;

    private String longitude;

    @Enumerated(EnumType.STRING)
    private final SocialType socialType = SocialType.KAKAO;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = List.of("USER");


    public KakaoUser(KakaoUserInfo kakaoUserInfo) {
        this.kakaoUserId = kakaoUserInfo.getKakaoUserId();
        this.nickname = kakaoUserInfo.getNickname();
        this.profileImageUrl = kakaoUserInfo.getProfileImageUrl();
        this.name = kakaoUserInfo.getName();
        this.email = kakaoUserInfo.getEmail();
        this.gender = kakaoUserInfo.getGender();
        this.ageRange = kakaoUserInfo.getAgeRange();
        this.appliedClass = kakaoUserInfo.getAppliedClass();
        this.latitude = kakaoUserInfo.getLatitude();
        this.longitude = kakaoUserInfo.getLongitude();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.name;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

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
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
