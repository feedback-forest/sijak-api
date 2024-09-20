package zerobase.sijak.dto.kakao;

import lombok.Data;
import lombok.Getter;

@Data
public class KakaoProfile {

    private Long id;
    private String connected_at;
    private Properties properties;
    private KakaoAccount kakao_account;

    @Getter
    public static class Properties {
        private String nickname;
        private String profile_image;
        private String thumbnail_image;
    }

    @Getter
    public static class KakaoAccount {
        private Boolean profile_needs_agreement;
        private Boolean profile_nickname_needs_agreement;
        private Boolean profile_image_needs_agreement;
        private Profile profile;
        private Boolean name_needs_agreement;
        private String name;
        private Boolean email_needs_agreement;
        private Boolean is_email_valid;
        private Boolean is_email_verified;
        private String email;
        private Boolean age_range_needs_agreement;
        private String age_range;
        private Boolean birthyear_needs_agreement;
        private String birthyear;
        private Boolean birthday_needs_agreement;
        private String birthday;
        private Boolean gender_needs_agreement;
        private String gender;
        private Boolean phone_number_needs_agreement;
        private String phone_number;

        @Getter
        public static class Profile {
            private String nickname;
            private String thumbnail_image_url;
            private String profile_image_url;
        }
    }
}
