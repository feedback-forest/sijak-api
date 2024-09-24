package zerobase.sijak.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.sijak.dto.HttpResponse;
import zerobase.sijak.dto.kakao.ResponseDTO;
import zerobase.sijak.service.KakaoService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoUserController {

    private final KakaoService kakaoService;

    @GetMapping("/index")
    public String login() {
        return "index";
    }

    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<HttpResponse> getToken(@RequestParam("code") String code) {
        log.info("get token");
        ResponseDTO responseDTO = kakaoService.createPrivateToken(code);

        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK, HttpStatus.OK.toString(), responseDTO));
    }

}