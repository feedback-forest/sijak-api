package zerobase.sijak.service;

import io.jsonwebtoken.Claims;
import jdk.jfr.Period;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.sijak.dto.*;
import zerobase.sijak.exception.EmailNotExistException;
import zerobase.sijak.exception.ErrorCode;
import zerobase.sijak.exception.IdNotExistException;
import zerobase.sijak.jwt.JwtTokenProvider;
import zerobase.sijak.persist.domain.Lecture;
import zerobase.sijak.persist.domain.Member;
import zerobase.sijak.persist.repository.LectureRepository;
import zerobase.sijak.persist.repository.MemberRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    private final double EARTH_RADIUS_KM = 6371.0;

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

    public Slice<LectureHomeResponse> readLectures(String token, Pageable pageable, double longitude, double latitude) {
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            log.info("readLectues -> 회원이 아닙니다.");
            Slice<Lecture> lectures = lectureRepository.findLecturesByDistance(latitude, longitude, 0.5, pageable);
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
            Slice<Lecture> lectures = lectureRepository.findLecturesByDistance(latitude, longitude, 0.5, pageable);
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

    public LectureDetailResponse readLecture(String token, Integer id, double latitude, double longitude) {


        Lecture lecture = lectureRepository
                .findById(id)
                .orElseThrow(() -> new IdNotExistException("해당 강의 id가 존재하지 않습니다.", ErrorCode.LECTURE_ID_NOT_EXIST));

        lecture.setView(lecture.getView() + 1);  // 조회수 증가
        lecture = lectureRepository.save(lecture);
        // 회원이 아닌 경우 -> 찜 정보가 노출이 되지 않는다.
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            log.info("회원이 아닙니다.");
            double dist = calculateDistance(latitude, longitude, lecture.getLatitude(), lecture.getLongitude()) * 1000;
            dist = Math.round(dist * 10.0) / 10.0;
            String distance = String.valueOf(dist) + "m";
            String estimatedTime = "도보 약 " + String.valueOf((int) (dist * 0.02)) + "분 이내";
            PeriodInfo periodInfo = new PeriodInfo(lecture.getStartDate(), lecture.getEndDate(), lecture.getTotal());
            List<PeriodInfo> periodInfoList = new ArrayList<>();
            periodInfoList.add(periodInfo);

            List<TeacherInfo> teacherInfoList = lecture.getTeachers().stream().map(teacher -> {
                List<CareerInfo> careerInfoList = teacher.getCareers().stream().map(career ->
                        new CareerInfo(career.getId(), career.getContent())).collect(Collectors.toList());

                return new TeacherInfo(teacher.getId(), teacher.getName(), careerInfoList);
            }).toList();

            LocalDateTime lastDate = lecture.getDeadline();
            LocalDateTime curDate = LocalDateTime.now();
            String deadline;
            if (lastDate == null) {
                lastDate = LocalDate.parse(lecture.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
                deadline = "D - " + String.valueOf(ChronoUnit.DAYS.between(lastDate, curDate) - 1);
            }
            else {
                deadline = "D - " + String.valueOf(ChronoUnit.DAYS.between(lastDate, curDate));
            }
            LectureDetailResponse lectureDetailResponse = LectureDetailResponse.builder()
                    .id(lecture.getId())
                    .name(lecture.getName())
                    .description(lecture.getDescription())
                    .price(lecture.getPrice())
                    .dayOfWeek(lecture.getDayOfWeek())
                    .time(lecture.getTime())
                    .capacity(lecture.getCapacity())
                    .link(lecture.getLink())
                    .period(periodInfoList)
                    .location(lecture.getLocation())
                    .status(lecture.isStatus())
                    .thumbnail(lecture.getThumbnail())
                    .address(lecture.getAddress())
                    .hostedBy(lecture.getCenterName())
                    .division(lecture.getDivision())
                    .condition(lecture.getTarget())
                    .category("미정")
                    .dDay(deadline)
                    .detail(lecture.getDescription())
                    .certification(lecture.getCertification())
                    .textBookName(lecture.getTextBookName())
                    .textBookPrice(lecture.getTextBookPrice())
                    .instructorName(teacherInfoList)
                    .need(lecture.getNeed())
                    .images(lecture.getImageUrls())
                    .heart(false)
                    .distance(distance)
                    .estimatedTime(estimatedTime)
                    .build();
            return lectureDetailResponse;
        }
        // 회원인 경우 -> 찜 정보가 노출이 된다.
        else {
            String jwtToken = token.substring(7);
            Claims claims = jwtTokenProvider.parseClaims(jwtToken);
            log.info("readLectures: token not null");
            log.info("회원입니다. email: {}", claims.getSubject());
            Member member = memberRepository.findByAccountEmail(claims.getSubject());
            if (member == null) {
                throw new EmailNotExistException("해당 유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);
            }

            double dist = calculateDistance(latitude, longitude, lecture.getLatitude(), lecture.getLongitude()) * 1000;
            dist = Math.round(dist * 10.0) / 10.0;
            String distance = String.valueOf(dist) + "m";
            String estimatedTime = "도보 약 " + String.valueOf((int) (dist * 0.02)) + "분 이내";
            PeriodInfo periodInfo = new PeriodInfo(lecture.getStartDate(), lecture.getEndDate(), lecture.getTotal());
            List<PeriodInfo> periodInfoList = new ArrayList<>();
            periodInfoList.add(periodInfo);

            List<TeacherInfo> teacherInfoList = lecture.getTeachers().stream().map(teacher -> {
                List<CareerInfo> careerInfoList = teacher.getCareers().stream().map(career ->
                        new CareerInfo(career.getId(), career.getContent())).collect(Collectors.toList());

                return new TeacherInfo(teacher.getId(), teacher.getName(), careerInfoList);
            }).toList();

            LocalDateTime lastDate = lecture.getDeadline();
            LocalDateTime curDate = LocalDateTime.now();
            String deadline;
            if (lastDate == null) {
                lastDate = LocalDate.parse(lecture.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
                deadline = "D - " + String.valueOf(ChronoUnit.DAYS.between(lastDate, curDate) - 1);
            }
            else {
                deadline = "D - " + String.valueOf(ChronoUnit.DAYS.between(lastDate, curDate));
            }

            LectureDetailResponse lectureDetailResponse = LectureDetailResponse.builder()
                    .id(lecture.getId())
                    .name(lecture.getName())
                    .description(lecture.getDescription())
                    .price(lecture.getPrice())
                    .dayOfWeek(lecture.getDayOfWeek())
                    .time(lecture.getTime())
                    .capacity(lecture.getCapacity())
                    .link(lecture.getLink())
                    .period(periodInfoList)
                    .location(lecture.getLocation())
                    .status(lecture.isStatus())
                    .thumbnail(lecture.getThumbnail())
                    .address(lecture.getAddress())
                    .hostedBy(lecture.getCenterName())
                    .division(lecture.getDivision())
                    .condition(lecture.getTarget())
                    .dDay(deadline)
                    .detail(lecture.getDescription())
                    .certification(lecture.getCertification())
                    .category("미정")
                    .textBookName(lecture.getTextBookName())
                    .textBookPrice(lecture.getTextBookPrice())
                    .instructorName(teacherInfoList)
                    .images(lecture.getImageUrls())
                    .need(lecture.getNeed())
                    .heart(heartService.isHearted(id, member.getId()))
                    .distance(distance)
                    .estimatedTime(estimatedTime)
                    .build();

            return lectureDetailResponse;
        }
    }

    public List<PickHomeResponse> getPickClasses(String token) {

        // 회원이 아닌 경우
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {

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
                            .heart(false)
                            .build()).collect(Collectors.toList());

        }
        // 회원이 아닌 경우
        else {
            String jwtToken = token.substring(7);
            Claims claims = jwtTokenProvider.parseClaims(jwtToken);
            log.info("readLectures: token not null");

            Member member = memberRepository.findByAccountEmail(claims.getSubject());
            if (member == null) {
                throw new EmailNotExistException("해당 유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);
            }

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
                            .heart(heartService.isHearted(lecture.getId(), member.getId()))
                            .build()).collect(Collectors.toList());
        }
    }


    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 위도와 경도를 라디안 단위로 변환
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // Haversine 공식
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 거리를 계산하여 반환 (단위: 킬로미터)
        return EARTH_RADIUS_KM * c;
    }

}
