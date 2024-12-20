package zerobase.sijak.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zerobase.sijak.dto.CommonResponse;
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
    public CommonResponse<?> readHearts(@RequestHeader("Authorization") String token,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(name = "mode") boolean mode) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LectureHomeResponse> hearts = heartService.readHearts(token, mode, pageable);

        Map<String, Object> totalList = new HashMap<>();
        totalList.put("data", hearts.getContent());
        totalList.put("hasNext", hearts.hasNext());
        totalList.put("totalPage", hearts.getTotalPages());

        return CommonResponse.of(totalList);
    }


    @PostMapping("/hearts")
    public CommonResponse<?> appendHearts(@RequestHeader("Authorization") String token, @RequestParam int lectureId) {

        heartService.appendHeart(token, lectureId);

        return CommonResponse.of("success");
    }

    @DeleteMapping("/hearts")
    public CommonResponse<?> deleteHearts(@RequestHeader("Authorization") String token, @RequestParam int lectureId) {

        heartService.deleteHeart(token, lectureId);

        return CommonResponse.of("success");
    }

    @DeleteMapping("/hearts/deactivates")
    public CommonResponse<?> deactivateHearts(@RequestHeader("Authorization") String token) {

        heartService.deleteDeactivatedHearts(token);

        return CommonResponse.of("success");
    }

}
