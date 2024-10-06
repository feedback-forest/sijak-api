package zerobase.sijak.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class GeoLocationNotExistException extends RuntimeException {

    private ErrorCode errorCode;

    public GeoLocationNotExistException(String message, ErrorCode errorCode) {
        super(message);
        log.info("EmailNotExistException -> message: {}, errorCode: {}", message, errorCode);
        this.errorCode = errorCode;
    }
}
