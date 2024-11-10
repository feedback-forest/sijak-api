package zerobase.sijak.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import zerobase.sijak.dto.CommonResponse;
import zerobase.sijak.service.LectureService;

@RestController
public class IndexController {

    private final LectureService lectureService;

    public IndexController(LectureService lectureService) {
        this.lectureService = lectureService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/api/seo")
    public CommonResponse<?> seo() {
        return CommonResponse.of(lectureService.getClassIds());
    }

}
