package zerobase.sijak.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlreadyHeartException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyHeartException(AlreadyHeartException e) {
        log.error("AlreadyHeartException", e);
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    @ExceptionHandler(AlreadyNicknameExistException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyNicknameExistException(AlreadyNicknameExistException e) {
        log.error("AlreadyNicknameExistException", e);
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    @ExceptionHandler(EmailNotExistException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotExistException(EmailNotExistException e) {
        log.error("EmailNotExistException", e);
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    @ExceptionHandler(HeartRemoveException.class)
    public ResponseEntity<ErrorResponse> handleHeartRemoveException(HeartRemoveException e) {
        log.error("HeartRemoveException", e);
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }


    @ExceptionHandler(IdNotExistException.class)
    public ResponseEntity<ErrorResponse> handleIdNotExistException(IdNotExistException e) {
        log.error("IdNotExistException", e);
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    @ExceptionHandler(InvalidNicknameException.class)
    public ResponseEntity<ErrorResponse> handleInvalidNicknameException(InvalidNicknameException e) {
        log.error("InvalidNicknameException", e);
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    @ExceptionHandler(LoginFailException.class)
    public ResponseEntity<ErrorResponse> handleLoginFailException(LoginFailException e) {
        log.error("LoginFailException", e);
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("handleException", e);
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INTER_SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
