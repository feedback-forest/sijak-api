package zerobase.sijak.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import zerobase.sijak.dto.CategoryRequest;
import zerobase.sijak.dto.LectureHomeResponse;
import zerobase.sijak.dto.SearchRequest;
import zerobase.sijak.dto.SearchResponse;
import zerobase.sijak.exception.Code;
import zerobase.sijak.exception.CustomException;
import zerobase.sijak.jwt.JwtTokenProvider;
import zerobase.sijak.persist.domain.Lecture;
import zerobase.sijak.persist.domain.Member;
import zerobase.sijak.persist.repository.LectureRepository;
import zerobase.sijak.persist.repository.MemberRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final HeartService heartService;

    public Slice<LectureHomeResponse> searchKeyword(String token, Pageable pageable, SearchRequest searchRequest) {
        String keyword = searchRequest.getKeyword();
        String location = searchRequest.getLocation();

        log.info("keyword: {}, location: {}", keyword, location);
        Slice<Lecture> searchedKeywordAndLocations = lectureRepository.searchKeywordAndLocation(pageable, keyword, location);
        log.info("string: {}", searchedKeywordAndLocations.toString());
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            log.info("readLectues -> 회원이 아닙니다.");
            List<LectureHomeResponse> lectureHomeResponseList = searchedKeywordAndLocations.getContent().stream()
                    .map(lecture -> {
                        String[] addressList = lecture.getAddress().split(" ");
                        String shortAddress = addressList[0] + " " + addressList[1];
                        return LectureHomeResponse.builder()
                                .id(lecture.getId())
                                .name(lecture.getName())
                                .time(lecture.getTime())
                                .thumbnail(lecture.getThumbnail())
                                .target(lecture.getTarget())
                                .division(lecture.getDivision())
                                .startDate(lecture.getStartDate())
                                .endDate(lecture.getEndDate())
                                .latitude(lecture.getLatitude())
                                .longitude(lecture.getLongitude())
                                .hostedBy(lecture.getCenterName())
                                .dayOfWeek(lecture.getDayOfWeek())
                                .price(lecture.getPrice())
                                .longAddress(lecture.getAddress())
                                .shortAddress(shortAddress)
                                .status(lecture.isStatus())
                                .link(lecture.getLink())
                                .heart(false).build();
                    }).toList();
            return new SliceImpl<>(lectureHomeResponseList, pageable, searchedKeywordAndLocations.hasNext());
        } else {
            log.info("readLectures -> 회원입니다.");
            String jwtToken = token.substring(7);
            Claims claims = jwtTokenProvider.parseClaims(jwtToken);
            log.info("readLectures: token not null");

            Member member = memberRepository.findByKakaoUserId(claims.getSubject()).orElseThrow(
                    () -> new CustomException(Code.KAKAO_ID_NOT_EXIST)
            );

            List<LectureHomeResponse> lectureHomeResponseList = searchedKeywordAndLocations.getContent().stream()
                    .map(lecture -> {
                        boolean isHeart = heartService.isHearted(lecture.getId(), member.getId());
                        String[] addressList = lecture.getAddress().split(" ");
                        String shortAddress = addressList[0] + " " + addressList[1];
                        return LectureHomeResponse.builder()
                                .id(lecture.getId())
                                .name(lecture.getName())
                                .time(lecture.getTime())
                                .division(lecture.getDivision())
                                .thumbnail(lecture.getThumbnail())
                                .target(lecture.getTarget())
                                .longAddress(lecture.getAddress())
                                .shortAddress(shortAddress)
                                .latitude(lecture.getLatitude())
                                .longitude(lecture.getLongitude())
                                .hostedBy(lecture.getCenterName())
                                .price(lecture.getPrice())
                                .status(lecture.isStatus())
                                .startDate(lecture.getStartDate())
                                .endDate(lecture.getEndDate())
                                .dayOfWeek(lecture.getDayOfWeek())
                                .link(lecture.getLink())
                                .heart(isHeart).build();
                    }).toList();

            return new SliceImpl<>(lectureHomeResponseList, pageable, searchedKeywordAndLocations.hasNext());
        }
    }

    public Slice<LectureHomeResponse> searchCategory(String token, Pageable pageable, CategoryRequest categoryRequest) {
        String category = categoryRequest.getCategory();
        String location = categoryRequest.getLocation();

        log.info("category: {}, location: {}", category, location);
        Slice<Lecture> searchedCategoryAndLocations = lectureRepository.searchCategoryAndLocation(pageable, category, location);
        log.info("string: {}", searchedCategoryAndLocations.toString());
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            log.info("readLectues -> 회원이 아닙니다.");
            List<LectureHomeResponse> lectureHomeResponseList = searchedCategoryAndLocations.getContent().stream()
                    .map(lecture -> {
                        String[] addressList = lecture.getAddress().split(" ");
                        String shortAddress = addressList[0] + " " + addressList[1];
                        return LectureHomeResponse.builder()
                                .id(lecture.getId())
                                .name(lecture.getName())
                                .time(lecture.getTime())
                                .thumbnail(lecture.getThumbnail())
                                .target(lecture.getTarget())
                                .division(lecture.getDivision())
                                .startDate(lecture.getStartDate())
                                .endDate(lecture.getEndDate())
                                .latitude(lecture.getLatitude())
                                .longitude(lecture.getLongitude())
                                .hostedBy(lecture.getCenterName())
                                .dayOfWeek(lecture.getDayOfWeek())
                                .price(lecture.getPrice())
                                .longAddress(lecture.getAddress())
                                .shortAddress(shortAddress)
                                .category(lecture.getCategory())
                                .status(lecture.isStatus())
                                .link(lecture.getLink())
                                .heart(false).build();
                    }).toList();
            return new SliceImpl<>(lectureHomeResponseList, pageable, searchedCategoryAndLocations.hasNext());
        } else {
            log.info("readLectures -> 회원입니다.");
            String jwtToken = token.substring(7);
            Claims claims = jwtTokenProvider.parseClaims(jwtToken);
            log.info("readLectures: token not null");

            Member member = memberRepository.findByKakaoUserId(claims.getSubject()).orElseThrow(
                    () -> new CustomException(Code.KAKAO_ID_NOT_EXIST)
            );

            log.info("readLectures: member email {}", member.getAccountEmail());
            List<LectureHomeResponse> lectureHomeResponseList = searchedCategoryAndLocations.getContent().stream()
                    .map(lecture -> {
                        boolean isHeart = heartService.isHearted(lecture.getId(), member.getId());
                        String[] addressList = lecture.getAddress().split(" ");
                        String shortAddress = addressList[0] + " " + addressList[1];
                        return LectureHomeResponse.builder()
                                .id(lecture.getId())
                                .name(lecture.getName())
                                .time(lecture.getTime())
                                .division(lecture.getDivision())
                                .thumbnail(lecture.getThumbnail())
                                .target(lecture.getTarget())
                                .longAddress(lecture.getAddress())
                                .shortAddress(shortAddress)
                                .latitude(lecture.getLatitude())
                                .longitude(lecture.getLongitude())
                                .hostedBy(lecture.getCenterName())
                                .category(lecture.getCategory())
                                .price(lecture.getPrice())
                                .status(lecture.isStatus())
                                .startDate(lecture.getStartDate())
                                .endDate(lecture.getEndDate())
                                .dayOfWeek(lecture.getDayOfWeek())
                                .link(lecture.getLink())
                                .heart(isHeart).build();
                    }).toList();

            return new SliceImpl<>(lectureHomeResponseList, pageable, searchedCategoryAndLocations.hasNext());
        }
    }

    public SearchResponse getMatchedKeyword(String keyword) {

        List<Lecture> lectures = lectureRepository.searchNamesByPrefix(keyword);

        List<String> names = lectures.stream()
                .map(Lecture::getName)
                .toList();

        return SearchResponse.builder()
                .names(names)
                .build();
    }

}
