package zerobase.sijak.exception;

import com.fasterxml.jackson.core.JsonParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import zerobase.sijak.dto.CommonResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected CommonResponse<String> handleException(CustomException e) {
        return CommonResponse.of(e.getErrorCode());
    }

}
