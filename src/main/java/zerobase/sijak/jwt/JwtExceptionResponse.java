package zerobase.sijak.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtExceptionResponse {

    private final Integer code;
    private final String message;
    private final String data = null;


}
