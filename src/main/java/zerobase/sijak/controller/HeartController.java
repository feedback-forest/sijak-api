package zerobase.sijak.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zerobase.sijak.dto.HttpResponse;
import zerobase.sijak.dto.LectureHomeResponse;
import zerobase.sijak.service.HeartService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HeartController {

    private final HeartService heartService;

    @GetMapping("/hearts")
    public ResponseEntity<HttpResponse> readHearts(@RequestHeader("Authorization") String token,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<LectureHomeResponse> hearts = heartService.readHearts(token, pageable);

        Map<String, Object> totalList = new HashMap<>();
        totalList.put("data", hearts.getContent());
        totalList.put("hasNext", hearts.hasNext());

        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), totalList));
    }


    @PostMapping("/hearts")
    public ResponseEntity<HttpResponse> appendHearts(@RequestHeader("Authorization") String token, @RequestParam int lectureId) {

        heartService.appendHeart(token, lectureId);

        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), "success"));
    }

    @DeleteMapping("/hearts")
    public ResponseEntity<HttpResponse> deleteHearts(@RequestHeader("Authorization") String token, @RequestParam int lectureId) {

        heartService.deleteHeart(token, lectureId);

        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK.value(), HttpStatus.OK.toString(), "success"));
    }

}
