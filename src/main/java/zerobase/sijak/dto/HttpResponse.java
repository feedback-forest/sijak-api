package zerobase.sijak.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
public class HttpResponse<T> {

    private HttpStatus status;
    private String message;
    private T data;

    public HttpResponse(final HttpStatus status, final String message) {
        this.status = status;
        this.message = message;
        this.data = null;
    }

    public static <T> HttpResponse<T> res(final HttpStatus status, final String message) {
        return res(status, message, null);
    }

    public static <T> HttpResponse<T> res(final HttpStatus status, final String message, final T data) {
        return HttpResponse.<T>builder()
                .status(status)
                .message(message)
                .data(data)
                .build();
    }


}