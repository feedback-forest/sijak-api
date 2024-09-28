package zerobase.sijak.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import zerobase.sijak.dto.kakao.TokenDTO;
import zerobase.sijak.dto.kakao.UserLoginRequestDTO;
import zerobase.sijak.jwt.KakaoUserService;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TokenController {

    private final KakaoUserService kakaoUserService;

    @PostMapping("/login")
    public TokenDTO login(@RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        String email = userLoginRequestDTO.getEmail();
        String nickname = userLoginRequestDTO.getNickname();
        TokenDTO tokenDTO = kakaoUserService.login(email, nickname);

        log.info("login : success / tokenDTO");
        return tokenDTO;
    }

}
