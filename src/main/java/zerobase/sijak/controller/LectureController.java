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
import zerobase.sijak.persist.domain.Lecture;
import zerobase.sijak.service.LectureService;

import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LectureController {

    private final LectureService lectureService;

    @GetMapping("/lectures")
    public ResponseEntity<HttpResponse> readLectures(@RequestHeader("Authorization") String token,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Slice<LectureHomeResponse> lectures = lectureService.readLectures(token, pageable);

        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK, HttpStatus.OK.toString(),
                Map.of("data", lectures.getContent(), "hasNext", lectures.hasNext())));
    }

    @GetMapping("/lectures/{id}")
    public ResponseEntity<HttpResponse> readLecture(@PathVariable int id) {
        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK, HttpStatus.OK.toString(), lectureService.readLecture(id)));
    }

    @GetMapping("/home")
    public ResponseEntity<HttpResponse> readLecturs() {
        return null;
    }

    @GetMapping("/mypage")
    public ResponseEntity<HttpResponse> readMypage() {
        return null;
    }

    @GetMapping("/wishes")
    public ResponseEntity<HttpResponse> readWishes() {
        return null;
    }

    @PostMapping("/wishes/{lecture_id}")
    public ResponseEntity<HttpResponse> createWish(@RequestHeader("Authorization") String token, @PathVariable int lecture_id) {
        lectureService.toggleHeart(token, lecture_id);
        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK, HttpStatus.OK.toString(), "success"));
    }


}
