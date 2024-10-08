package zerobase.sijak.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class AlreadyHeartException extends RuntimeException {

    private ErrorCode errorCode;

    public AlreadyHeartException(String message, ErrorCode errorCode) {
        super(message);
        log.info("AlreadyHeartException -> message: {}, errorCode: {}", message, errorCode);
        this.errorCode = errorCode;
    }

}
