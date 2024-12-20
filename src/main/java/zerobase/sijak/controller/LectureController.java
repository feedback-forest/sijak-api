package zerobase.sijak.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zerobase.sijak.dto.*;
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

    @PostMapping("/home")
    public CommonResponse<?> readHome(@RequestHeader("Authorization") String token,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "4") int size) {


        Pageable pageable = PageRequest.of(page, size);
        Slice<LectureHomeResponse> lectures = lectureService.readHome(token, pageable);
        List<PickHomeResponse> pickClasses = lectureService.getPickClasses(token);
        List<MarkerResponse> markerClasses = lectureService.getMarkerClass();

        Map<String, Object> totalList = new HashMap<>();
        totalList.put("data", lectures.getContent());
        totalList.put("hasNext", lectures.hasNext());
        totalList.put("pickClasses", pickClasses);
        totalList.put("markerClasses", markerClasses);

        return CommonResponse.of(totalList);
    }

    @PostMapping("/lectures")
    public CommonResponse<?> readLectures(@RequestHeader("Authorization") String token,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "9") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Slice<LectureHomeResponse> lectures = lectureService.readLectures(token, pageable);

        Map<String, Object> totalList = new HashMap<>();
        totalList.put("data", lectures.getContent());
        totalList.put("hasNext", lectures.hasNext());

        return CommonResponse.of(totalList);
    }

    @PostMapping("/location")
    public CommonResponse<?> readLocation(@RequestHeader("Authorization") String token,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "4") int size,
                                          @RequestParam(value = "location") String location) {

        Pageable pageable = PageRequest.of(page, size);
        Slice<LectureHomeResponse> lectures = lectureService.readLectureByLocation(token, location, pageable);

        Map<String, Object> lectureByLocationList = new HashMap<>();
        lectureByLocationList.put("data", lectures.getContent());
        lectureByLocationList.put("hasNext", lectures.hasNext());

        return CommonResponse.of(lectureByLocationList);
    }

    @PostMapping("/lectures/{id}")
    public CommonResponse<?> readLecture(@RequestHeader("Authorization") String token, @PathVariable int id) {

        LectureDetailResponse lectureDetailResponse = lectureService.readLecture(token, id);

        return CommonResponse.of(lectureDetailResponse);
    }


}
