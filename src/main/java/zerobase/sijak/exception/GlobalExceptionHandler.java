package zerobase.sijak.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleGeoLocationNotExistException(GeoLocationNotExistException e) {
        log.error("GeoLocationNotExistException", e);
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode());
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleInterruptedException(InterruptedException e) {
        log.error("InterruptedException", e);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "크롤링 작업 도중 실패했습니다.", "CRAWLING FAILED");
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseException(JsonParseException e) {
        log.error("JsonParseException", e);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "사용자 주소를 얻어오는 데 실패했습니다.", "ADDRESS PROCESSING FAILED");
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

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
