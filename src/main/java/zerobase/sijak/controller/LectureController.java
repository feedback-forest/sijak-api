package zerobase.sijak.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zerobase.sijak.dto.HttpResponse;
import zerobase.sijak.persist.domain.Lecture;
import zerobase.sijak.service.LectureService;

import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;

//    @GetMapping("/api/lectures")
//    public ResponseEntity<HttpResponse> readLectures() {
//        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK, HttpStatus.OK.toString(), lectureService.readLectures()));
//    }

    @GetMapping("/api/lectures")
    public ResponseEntity<HttpResponse> readLectures(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Slice<Lecture> lectures = lectureService.readLectures(pageable);

        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK, HttpStatus.OK.toString(), Map.of("data", lectures.getContent(), "hasNext", lectures.hasNext())));


    }

    @GetMapping("/api/lectures/{id}")
    public ResponseEntity<HttpResponse> readLecture(@PathVariable int id) {
        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK, HttpStatus.OK.toString(), lectureService.readLecture(id)));
    }

    @GetMapping("/api/home")
    public ResponseEntity<HttpResponse> readLecturs() {
        return null;
    }

    @GetMapping("/api/mypage")
    public ResponseEntity<HttpResponse> readMypage() {
        return null;
    }

    @GetMapping("/api/wishes")
    public ResponseEntity<HttpResponse> readWishes() {
        return null;
    }


}
