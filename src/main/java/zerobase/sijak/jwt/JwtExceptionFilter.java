package zerobase.sijak.jwt;

import com.google.gson.JsonObject;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import zerobase.sijak.exception.Code;
import zerobase.sijak.exception.CustomException;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        try {
            chain.doFilter(request, response);
        } catch (CustomException ex) {
            String message = ex.getMessage();
            log.info("error Message: {}", message);
            if (Code.INVALID_ACCESS_TOKEN.getMessage().equals(message)) {
                setResponse(response, Code.INVALID_ACCESS_TOKEN);
            } else if (Code.EXPIRED_ACCESS_TOKEN.getMessage().equals(message)) {
                setResponse(response, Code.EXPIRED_ACCESS_TOKEN);
            } else if (Code.UNSUPPORTED_ACCESS_TOKEN.getMessage().equals(message)) {
                setResponse(response, Code.UNSUPPORTED_ACCESS_TOKEN);
            } else if (Code.WRONG_TYPE_ACCESS_TOKEN.getMessage().equals(message)) {
                setResponse(response, Code.WRONG_TYPE_ACCESS_TOKEN);
            } else {
                setResponse(response, Code.USER_FORBIDDEN);
            }
        }
    }

    private void setResponse(HttpServletResponse response, Code errorCode) throws RuntimeException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("code", errorCode.getCode());
        jsonObject.addProperty("message", errorCode.getMessage());
        jsonObject.add("data", null);
        response.getWriter().write(String.valueOf(jsonObject));
    }

}
