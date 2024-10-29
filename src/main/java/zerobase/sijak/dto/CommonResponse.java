package zerobase.sijak.dto;

import lombok.Builder;
import lombok.Getter;
import zerobase.sijak.exception.Code;

@Getter
@Builder
public class CommonResponse<T> {

    private final Integer code;
    private final String message;
    private final T data;


    public static <T> CommonResponse<T> of(T data) {
        return new CommonResponseBuilder<T>()
                .code(Code.OK.getCode())
                .message(Code.OK.getMessage())
                .data(data).build();
    }

    public static CommonResponse<String> of(Code errorCode) {
        return new CommonResponseBuilder<String>()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .data(null).build();
    }

}
