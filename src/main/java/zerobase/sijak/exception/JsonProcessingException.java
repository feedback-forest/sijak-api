package zerobase.sijak.exception;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class JsonProcessingException extends RuntimeException {

    private ErrorCode errorCode;

    public JsonProcessingException(String message, ErrorCode errorCode) {
        super(message);
        log.info("JsonProcessingException -> message: {}, errorCode: {}", message, errorCode);
        this.errorCode = errorCode;
    }

}
