package zerobase.sijak.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.sijak.dto.LectureDetailResponse;
import zerobase.sijak.dto.LectureHomeResponse;
import zerobase.sijak.dto.PickHomeResponse;
import zerobase.sijak.exception.EmailNotExistException;
import zerobase.sijak.exception.ErrorCode;
import zerobase.sijak.exception.IdNotExistException;
import zerobase.sijak.jwt.JwtTokenProvider;
import zerobase.sijak.persist.domain.Lecture;
import zerobase.sijak.persist.domain.Member;
import zerobase.sijak.persist.repository.LectureRepository;
import zerobase.sijak.persist.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LectureService {

    private final HeartService heartService;
    private final KakaoService kakaoService;
    private final LectureRepository lectureRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    private final static double EARTH_RADIUS_KM = 6371.0;

    public Slice<LectureHomeResponse> readHome(String token, Pageable pageable, double longitude, double latitude) {

        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            log.info("readHome -> 회원이 아닙니다.");
            Slice<Lecture> lectures = lectureRepository.findLecturesByDistance(latitude, longitude, 0.5, pageable);
            List<LectureHomeResponse> lectureHomeResponseList = lectures.getContent().stream()
                    .map(lecture -> {
                        String[] addressList = lecture.getAddress().split(" ");
                        String shortAddress = addressList[0] + " " + addressList[1];
                        return LectureHomeResponse.builder()
                                .id(lecture.getId())
                                .name(lecture.getName())
                                .thumbnail(lecture.getThumbnail())
                                .startDate(lecture.getStartDate())
                                .endDate(lecture.getEndDate())
                                .dayOfWeek(lecture.getDayOfWeek())
                                .time(lecture.getTime())
                                .target(lecture.getTarget())
                                .status(lecture.isStatus())
                                .address(shortAddress)
                                .link(lecture.getLink())
                                .heart(false).build();
                    }).toList();
            return new SliceImpl<>(lectureHomeResponseList, pageable, lectures.hasNext());
        } else {
            log.info("readHome -> 회원입니다.");
            String jwtToken = token.substring(7);
            Claims claims = jwtTokenProvider.parseClaims(jwtToken);
            log.info("readLectures: token not null");

            Member member = memberRepository.findByAccountEmail(claims.getSubject());
            if (member == null) {
                throw new EmailNotExistException("해당 유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);
            }

            log.info("readLectures: member email {}", member.getAccountEmail());
            Slice<Lecture> lectures = lectureRepository.findLecturesByDistance(latitude, longitude, 0.5, pageable);
            List<LectureHomeResponse> lectureHomeResponseList = lectures.getContent().stream()
                    .map(lecture -> {
                        boolean isHeart = heartService.isHearted(lecture.getId(), member.getId());
                        String[] addressList = lecture.getAddress().split(" ");
                        String shortAddress = addressList[0] + " " + addressList[1];
                        return LectureHomeResponse.builder()
                                .id(lecture.getId())
                                .name(lecture.getName())
                                .thumbnail(lecture.getThumbnail())
                                .time(lecture.getTime())
                                .startDate(lecture.getStartDate())
                                .endDate(lecture.getEndDate())
                                .dayOfWeek(lecture.getDayOfWeek())
                                .target(lecture.getTarget())
                                .status(lecture.isStatus())
                                .address(shortAddress)
                                .link(lecture.getLink())
                                .heart(isHeart).build();
                    }).toList();

            return new SliceImpl<>(lectureHomeResponseList, pageable, lectures.hasNext());
        }
    }

    public Slice<LectureHomeResponse> readLectures(String token, Pageable pageable) {
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            log.info("readLectues -> 회원이 아닙니다.");
            Slice<Lecture> lectures = lectureRepository.findAll(pageable);
            List<LectureHomeResponse> lectureHomeResponseList = lectures.getContent().stream()
                    .map(lecture -> {
                        String[] addressList = lecture.getAddress().split(" ");
                        String shortAddress = addressList[0] + " " + addressList[1];
                        return LectureHomeResponse.builder()
                                .id(lecture.getId())
                                .name(lecture.getName())
                                .time(lecture.getTime())
                                .thumbnail(lecture.getThumbnail())
                                .target(lecture.getTarget())
                                .startDate(lecture.getStartDate())
                                .endDate(lecture.getEndDate())
                                .dayOfWeek(lecture.getDayOfWeek())
                                .address(shortAddress)
                                .status(lecture.isStatus())
                                .link(lecture.getLink())
                                .heart(false).build();
                    }).toList();
            return new SliceImpl<>(lectureHomeResponseList, pageable, lectures.hasNext());
        } else {
            log.info("readLectures -> 회원입니다.");
            String jwtToken = token.substring(7);
            Claims claims = jwtTokenProvider.parseClaims(jwtToken);
            log.info("readLectures: token not null");

            Member member = memberRepository.findByAccountEmail(claims.getSubject());
            if (member == null) {
                throw new EmailNotExistException("해당 유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);
            }

            log.info("readLectures: member email {}", member.getAccountEmail());
            Slice<Lecture> lectures = lectureRepository.findAll(pageable);
            List<LectureHomeResponse> lectureHomeResponseList = lectures.getContent().stream()
                    .map(lecture -> {
                        boolean isHeart = heartService.isHearted(lecture.getId(), member.getId());
                        String[] addressList = lecture.getAddress().split(" ");
                        String shortAddress = addressList[0] + " " + addressList[1];
                        return LectureHomeResponse.builder()
                                .id(lecture.getId())
                                .name(lecture.getName())
                                .time(lecture.getTime())
                                .thumbnail(lecture.getThumbnail())
                                .target(lecture.getTarget())
                                .address(shortAddress)
                                .status(lecture.isStatus())
                                .startDate(lecture.getStartDate())
                                .endDate(lecture.getEndDate())
                                .dayOfWeek(lecture.getDayOfWeek())
                                .link(lecture.getLink())
                                .heart(isHeart).build();
                    }).toList();

            return new SliceImpl<>(lectureHomeResponseList, pageable, lectures.hasNext());
        }
    }

    public LectureDetailResponse readLecture(Integer id) {

        Lecture lecture = lectureRepository
                .findById(id)
                .orElseThrow(() -> new IdNotExistException("해당 강의 id가 존재하지 않습니다.", ErrorCode.LECTURE_ID_NOT_EXIST));

        lecture.setView(lecture.getView() + 1);
        lectureRepository.save(lecture);
        LectureDetailResponse lectureDetailResponse = LectureDetailResponse.builder()
                .id(lecture.getId())
                .name(lecture.getName())
                .description(lecture.getDescription())
                .price(lecture.getPrice())
                .dayOfWeek(lecture.getDayOfWeek())
                .time(lecture.getTime())
                .capacity(lecture.getCapacity())
                .link(lecture.getLink())
                .location(lecture.getLocation())
                .status(lecture.isStatus())
                .thumbnail(lecture.getThumbnail())
                .address(lecture.getAddress())
                .hostedBy(lecture.getCenterName())
                .division(lecture.getDivision())
                .condition(lecture.getTarget())
                .detail(lecture.getDescription())
                .certification(lecture.getCertification())
                .textBookName(lecture.getTextBookName())
                .textBookPrice(lecture.getTextBookPrice())
                .need(lecture.getNeed())
                .build();

        return lectureDetailResponse;
    }

    public List<PickHomeResponse> getPickClasses() {
        List<Lecture> topLectures = lectureRepository.findTop6ByOrderByViewDesc();

        return topLectures.stream()
                .map(lecture -> PickHomeResponse.builder()
                        .id(lecture.getId())
                        .view(lecture.getView())
                        .name(lecture.getName())
                        .time(lecture.getTime())
                        .thumbnail(lecture.getThumbnail())
                        .startDate(lecture.getStartDate())
                        .endDate(lecture.getEndDate())
                        .dayOfWeek(lecture.getDayOfWeek())
                        .status(lecture.isStatus())
                        .target(lecture.getTarget())
                        .link(lecture.getLink())
                        .build()).collect(Collectors.toList());
    }

}
