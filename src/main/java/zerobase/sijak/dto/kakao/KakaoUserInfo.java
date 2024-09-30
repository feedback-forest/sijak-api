package zerobase.sijak.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserInfo {

    @JsonProperty("kakao_user_id")
    private Long kakaoUserId;

    private String nickname;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    private String birth;

    @JsonProperty("phone_number")
    private String phoneNumber;

    private String name;

    private String email;

    private String gender;

    @JsonProperty("age_range")
    private String ageRange;

}
