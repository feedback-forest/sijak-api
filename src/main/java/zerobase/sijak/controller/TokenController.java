package zerobase.sijak.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.sijak.dto.CommonResponse;
import zerobase.sijak.dto.kakao.TokenDTO;
import zerobase.sijak.dto.kakao.TokenRequest;
import zerobase.sijak.dto.kakao.UserLoginRequestDTO;
import zerobase.sijak.jwt.KakaoUserService;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TokenController {

    private final KakaoUserService kakaoUserService;

    @PostMapping("/login")
    public CommonResponse<?> login(@RequestBody UserLoginRequestDTO userLoginRequestDTO) {

        String email = userLoginRequestDTO.getEmail();
        String nickname = userLoginRequestDTO.getNickname();
        TokenDTO tokenDTO = kakaoUserService.login(email, nickname);

        log.info("login : success / tokenDTO");
        return CommonResponse.of(tokenDTO);
    }

    @PostMapping("/reissue")
    public CommonResponse<?> reissue(@RequestBody TokenRequest tokenRequest,
                                          HttpServletResponse response) {
        log.info("response: {}", response);
        log.info("response: {}", response.getStatus());
        TokenDTO tokenDTO = kakaoUserService.reissue(tokenRequest, response);

        return CommonResponse.of(tokenDTO);

    }

}
