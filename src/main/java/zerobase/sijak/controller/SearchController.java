package zerobase.sijak.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;
import zerobase.sijak.dto.*;
import zerobase.sijak.service.SearchService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/")
    public CommonResponse<?> getMatchedKeyword(@RequestParam String keyword) {
        SearchResponse searchResponse = searchService.getMatchedKeyword(keyword);
        return CommonResponse.of(searchResponse);
    }

    @GetMapping("/keyword")
    public CommonResponse<?> searchKeyword(@RequestHeader("Authorization") String token,
                                           @RequestBody SearchRequest searchRequest,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "9") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Slice<LectureHomeResponse> lectures = searchService.searchKeyword(token, pageable, searchRequest);

        Map<String, Object> totalList = new HashMap<>();
        totalList.put("data", lectures.getContent());
        totalList.put("hasNext", lectures.hasNext());

        return CommonResponse.of(totalList);
    }

    @GetMapping("/category")
    public CommonResponse<?> searchCategory(@RequestHeader("Authorization") String token,
                                            @RequestBody CategoryRequest categoryRequest,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "9") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Slice<LectureHomeResponse> lectures = searchService.searchCategory(token, pageable, categoryRequest);

        Map<String, Object> totalList = new HashMap<>();
        totalList.put("data", lectures.getContent());
        totalList.put("hasNext", lectures.hasNext());
        return CommonResponse.of(totalList);
    }
}
