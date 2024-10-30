package zerobase.sijak.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;
import zerobase.sijak.dto.CommonResponse;
import zerobase.sijak.dto.LectureHomeResponse;
import zerobase.sijak.dto.SearchRequest;
import zerobase.sijak.service.SearchService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class SearchController {


    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public CommonResponse<?> search(@RequestHeader("Authorization") String token,
                                    @RequestBody SearchRequest searchRequest,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "9") int size
                                    ) {

        Pageable pageable = PageRequest.of(page, size);
        Slice<LectureHomeResponse> lectures = searchService.search(token, pageable, searchRequest);

        Map<String, Object> totalList = new HashMap<>();
        totalList.put("data", lectures.getContent());
        totalList.put("hasNext", lectures.hasNext());

        return CommonResponse.of(totalList);
    }


}
