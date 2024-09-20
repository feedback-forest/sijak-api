package zerobase.sijak.dto.kakao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenDTO {

    //인증타입
    private String grantType;
    private String accessToken;
    private String refreshToken;
}
