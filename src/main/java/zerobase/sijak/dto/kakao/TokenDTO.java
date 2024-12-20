package zerobase.sijak.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TokenDTO {

    @JsonProperty("grant_type")
    private String grantType; // Bearer

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("access_token_expires_in")
    private long accessTokenExpiresIn;

}
