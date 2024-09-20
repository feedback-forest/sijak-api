package zerobase.sijak.persist.domain;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @Column(name = "kakao_user_id", unique = true, nullable = false)
    private Long kakaoUserId;

    @Column(name = "account_email", unique = true, nullable = false)
    private String accountEmail;

    @Column(name = "profile_nickname")
    private String profileNickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "user_name")
    private String name;

    @Column(name = "gender")
    private String gender;

    @Column(name = "age_range")
    private String ageRange;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "birth")
    private LocalDateTime birth;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Heart> hearts = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();


}
