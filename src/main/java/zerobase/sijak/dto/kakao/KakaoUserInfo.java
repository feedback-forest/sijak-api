package zerobase.sijak.dto.kakao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoUserInfo {

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
}
