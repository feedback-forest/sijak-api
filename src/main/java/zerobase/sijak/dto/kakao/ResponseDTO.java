package zerobase.sijak.dto.kakao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO {

    KakaoProfile kakaoProfile;
    TokenDTO tokenDTO;

}

