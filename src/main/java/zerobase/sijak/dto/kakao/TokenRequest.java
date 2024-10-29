package zerobase.sijak.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest {

    @NotNull
    @JsonProperty("access_token")
    private String accessToken;

    @NotNull
    @JsonProperty("refresh_token")
    private String refreshToken;

}
