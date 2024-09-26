package zerobase.sijak.persist.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zerobase.sijak.dto.kakao.KakaoUserInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer id;

    @Column(name = "kakao_user_id", unique = true, nullable = false)
    @JsonProperty("kakao_user_id")
    private Long kakaoUserId;

    @Column(name = "account_email", unique = true, nullable = false)
    @JsonProperty("account_email")
    private String accountEmail;

    @Column(name = "profile_nickname")
    @JsonProperty("profile_nickname")
    private String profileNickname;

    @Column(name = "profile_image_url")
    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    @Column(name = "user_name")
    private String name;

    @Column(name = "gender")
    private String gender;

    @Column(name = "age_range")
    @JsonProperty("age_range")
    private String ageRange;

    @Column(name = "address")
    private String address;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "birth")
    private String birth;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Heart> hearts = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    public Member(KakaoUserInfo kakaoUserInfo) {
        this.kakaoUserId = kakaoUserInfo.getKakaoUserId();
        this.accountEmail = kakaoUserInfo.getEmail();
        this.profileNickname = kakaoUserInfo.getNickname();
        this.birth = kakaoUserInfo.getBirth();
        this.profileImageUrl = kakaoUserInfo.getProfileImageUrl();
        this.name = kakaoUserInfo.getName();
        this.gender = kakaoUserInfo.getGender();
        this.ageRange = kakaoUserInfo.getAgeRange();

    }

}