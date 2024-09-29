package zerobase.sijak.exception;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class AlreadyNicknameExistException extends RuntimeException {

    private ErrorCode errorCode;

    public AlreadyNicknameExistException(String message, ErrorCode errorCode) {
        super(message);
        log.info("IdNotExistException -> message: {}, errorCode: {}", message, errorCode);
        this.errorCode = errorCode;
    }


}
