package zerobase.sijak.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;
import reactor.core.publisher.Mono;
import zerobase.sijak.dto.*;
import zerobase.sijak.dto.kakao.ResponseDTO;
import zerobase.sijak.service.KakaoService;
import zerobase.sijak.service.NicknameService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class KakaoUserController {

    private final KakaoService kakaoService;
    private final NicknameService nicknameService;

    @GetMapping("/login/callback")
    public ResponseEntity<HttpResponse> getToken(@RequestParam("code") String code) {
        log.info("get token");
        log.info("code: {}", code);
        ResponseDTO responseDTO = kakaoService.createPrivateToken(code);

        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), responseDTO));
    }

    @PostMapping("/api/logout")
    public ResponseEntity<HttpResponse> logout(@RequestHeader("Authorization") String token) {
        log.info("logout");
        kakaoService.logout(token);
        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), "success"));
    }

    @PostMapping("/api/agree")
    public ResponseEntity<HttpResponse> agree(@RequestHeader("Authorization") String token, @RequestBody AgreeInfo agreeInfo) {
        log.info("agree");
        kakaoService.saveAgree(token, agreeInfo);

        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), "success"));
    }

    @PostMapping("/api/nickname/validate")
    public ResponseEntity<HttpResponse> setNickname(@RequestHeader("Authorization") String token,
                                                    @RequestParam(name = "nickname") String nickname) {

        NicknameRequest nicknameRequest = NicknameRequest.builder().nickname(nickname).build();
        kakaoService.validateNickname(token, nicknameRequest);
        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), "success"));
    }

    @PostMapping("/api/nickname")
    public ResponseEntity<HttpResponse> updateNickname(@RequestHeader("Authorization") String token,
                                                       @RequestBody SignUpRequest signUpRequest) {

        kakaoService.setNickname(token, signUpRequest);
        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), "success"));
    }

    @GetMapping("/api/nickname")
    public ResponseEntity<HttpResponse> setBasicNickname() {

        String nickname = nicknameService.generate();
        NicknameResponse nicknameResponse = NicknameResponse.builder().nickname(nickname).build();

        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), nicknameResponse));
    }

    @PostMapping("/api/nickname/random")
    public ResponseEntity<HttpResponse> randomNickname() {

        String nickname = nicknameService.generate();
        NicknameResponse nicknameResponse = NicknameResponse.builder().nickname(nickname).build();

        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), nicknameResponse));
    }

    @GetMapping("/api/mypage")
    public ResponseEntity<HttpResponse> getMyPage(@RequestHeader("Authorization") String token) {
        MyPageResponse myPageResponse = kakaoService.getMyPage(token);
        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), myPageResponse));
    }

    @PatchMapping("/api/mypage/address")
    public ResponseEntity<HttpResponse> updateAddress(@RequestHeader("Authorization") String token,
                                                      @RequestBody PositionInfo positionInfo) throws JsonProcessingException {
        GeoResponse geoResponse = kakaoService.updateAddress(token, positionInfo);
        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), geoResponse));
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


    @GetMapping("/api/user/{id}")
    public ResponseEntity<HttpResponse> getUser(@RequestHeader("Authorization") String token, @PathVariable("id") int id) {

        MyPageResponse myPageResponse = kakaoService.getUserMypage(token, id);

        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), myPageResponse));
    }
}