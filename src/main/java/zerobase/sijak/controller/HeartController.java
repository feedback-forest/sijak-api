package zerobase.sijak.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zerobase.sijak.dto.HttpResponse;
import zerobase.sijak.persist.domain.Lecture;
import zerobase.sijak.service.HeartService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HeartController {

    private final HeartService heartService;

    @GetMapping("/hearts")
    public ResponseEntity<HttpResponse> readHearts(@RequestHeader("Authorization") String token) {

        List<Lecture> lectures = heartService.readHearts(token);
        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK, HttpStatus.OK.toString(), lectures));
    }


    @PostMapping("/hearts")
    public ResponseEntity<HttpResponse> appendHearts(@RequestHeader("Authorization") String token, @RequestParam int lectureId) {

        heartService.appendHeart(token, lectureId);

        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK, HttpStatus.OK.toString(), "success"));
    }

    @DeleteMapping("/hearts")
    public ResponseEntity<HttpResponse> deleteHearts(@RequestHeader("Authorization") String token, @RequestParam int lectureId) {

        heartService.deleteHeart(token, lectureId);

        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK, HttpStatus.OK.toString(), "success"));
    }

}
