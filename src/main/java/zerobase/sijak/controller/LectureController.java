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
import zerobase.sijak.dto.LectureAndPickResponse;
import zerobase.sijak.dto.LectureHomeResponse;
import zerobase.sijak.dto.PickHomeResponse;
import zerobase.sijak.persist.domain.Lecture;
import zerobase.sijak.service.LectureService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LectureController {

    private final LectureService lectureService;

    @GetMapping("/home")
    public ResponseEntity<HttpResponse> readHome(@RequestHeader("Authorization") String token,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam("longitude") double longitude,
                                                 @RequestParam("latitude") double latitude) {

        Pageable pageable = PageRequest.of(page, size);
        Slice<LectureHomeResponse> lectures = lectureService.readHome(token, pageable, longitude, latitude);
        List<PickHomeResponse> pickClasses = lectureService.getPickClasses();

        Map<String, Object> totalList = new HashMap<>();
        totalList.put("data", lectures.getContent());
        totalList.put("hasNext", lectures.hasNext());
        totalList.put("pickClasses", pickClasses);

        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK, HttpStatus.OK.toString(), totalList));
    }

    @GetMapping("/lectures")
    public ResponseEntity<HttpResponse> readLectures(@RequestHeader("Authorization") String token,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<LectureHomeResponse> lectures = lectureService.readLectures(token, pageable);
        List<PickHomeResponse> pickClasses = lectureService.getPickClasses();

        Map<String, Object> totalList = new HashMap<>();
        totalList.put("data", lectures.getContent());
        totalList.put("hasNext", lectures.hasNext());

        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK, HttpStatus.OK.toString(), totalList));
    }


    // Map.of("data", lectures.getContent(), "hasNext", lectures.hasNext()
    @GetMapping("/lectures/{id}")
    public ResponseEntity<HttpResponse> readLecture(@PathVariable int id) {
        return ResponseEntity.ok(HttpResponse.res(HttpStatus.OK, HttpStatus.OK.toString(), lectureService.readLecture(id)));
    }


}
