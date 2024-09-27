package zerobase.sijak.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import zerobase.sijak.dto.HttpResponse;
import zerobase.sijak.dto.MyPageParam;
import zerobase.sijak.dto.MyPageRequest;
import zerobase.sijak.dto.kakao.ResponseDTO;
import zerobase.sijak.persist.domain.Member;
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

    @GetMapping("/mypage")
    public ResponseEntity<HttpResponse> getMyPage(@RequestHeader("Authorization") String token) {
        Member member = kakaoService.getMyPage(token);
        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK, HttpStatus.OK.toString(), member));
    }

    @PostMapping("/mypage")
    public ResponseEntity<HttpResponse> updateMyPage(@RequestBody @Valid MyPageRequest myPageRequest) {

        MyPageParam myPageParam = MyPageParam.builder()
                .nickname(myPageRequest.getNickname())
                .address(myPageRequest.getAddress()).build();

        kakaoService.updateMyPage(myPageParam);


        return null;
    }


}