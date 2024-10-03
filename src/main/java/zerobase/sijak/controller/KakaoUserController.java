package zerobase.sijak.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zerobase.sijak.dto.*;
import zerobase.sijak.dto.kakao.ResponseDTO;
import zerobase.sijak.service.KakaoService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoUserController {

    private final KakaoService kakaoService;

    @GetMapping("/login/callback")
    public ResponseEntity<HttpResponse> getToken(@RequestParam("code") String code) {
        log.info("get token");
        log.info("code: {}", code);
        ResponseDTO responseDTO = kakaoService.createPrivateToken(code);

        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), responseDTO));
    }

    @PostMapping("/api/nickname/validate")
    public ResponseEntity<HttpResponse> setNickname(@RequestParam(name = "nickname") String nickname) {

        NicknameRequest nicknameRequest = NicknameRequest.builder().nickname(nickname).build();
        kakaoService.validateNickname(nicknameRequest);
        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), "success"));
    }

    @PostMapping("/api/nickname")
    public ResponseEntity<HttpResponse> updateNickname(@RequestHeader("Authorization") String token,
                                                       @RequestParam(name = "nickname") String nickname) {

        NicknameRequest nicknameRequest = NicknameRequest.builder().nickname(nickname).build();
        kakaoService.setNickname(token, nicknameRequest);
        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), "success"));
    }

    @GetMapping("/api/mypage")
    public ResponseEntity<HttpResponse> getMyPage(@RequestHeader("Authorization") String token) {
        MyPageResponse myPageResponse = kakaoService.getMyPage(token);
        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), myPageResponse));
    }

    @PatchMapping("/api/mypage/address")
    public ResponseEntity<HttpResponse> updateAddress(@RequestHeader("Authorization") String token,
                                                      @RequestBody PositionInfo positionInfo) throws JsonProcessingException {
        kakaoService.updateAddress(token, positionInfo);
        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), "success"));
    }

    @PatchMapping("/api/mypage")
    public ResponseEntity<HttpResponse> updateMyPage(@RequestHeader("Authorization") String token,
                                                     @RequestBody MyPageRequest myPageRequest) {

        MyPageParam myPageParam = MyPageParam.builder()
                .nickname(myPageRequest.getNickname())
                .address(myPageRequest.getAddress()).build();

        kakaoService.updateMyPage(token, myPageParam);
        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), "success"));
    }
}