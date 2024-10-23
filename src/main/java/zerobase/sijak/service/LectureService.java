package zerobase.sijak.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.sijak.dto.*;
import zerobase.sijak.exception.Code;
import zerobase.sijak.exception.CustomException;
import zerobase.sijak.jwt.JwtTokenProvider;
import zerobase.sijak.persist.domain.Educate;
import zerobase.sijak.persist.domain.Lecture;
import zerobase.sijak.persist.domain.Member;
import zerobase.sijak.persist.repository.EducateRepository;
import zerobase.sijak.persist.repository.LectureRepository;
import zerobase.sijak.persist.repository.MemberRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LectureService {

    private final HeartService heartService;
    private final LectureRepository lectureRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    private final double EARTH_RADIUS_KM = 6371.0;
    private final EducateRepository educateRepository;

    public Slice<LectureHomeResponse> readHome(String token, Pageable pageable) {

        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            log.info("readHome -> 회원이 아닙니다.");

            Slice<Lecture> lectures = lectureRepository.findAllByOrderByStatusDescViewDesc(pageable);
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
                                .division(lecture.getDivision())
                                .latitude(lecture.getLatitude())
                                .longitude(lecture.getLongitude())
                                .hostedBy(lecture.getCenterName())
                                .dayOfWeek(lecture.getDayOfWeek())
                                .time(lecture.getTime())
                                .target(lecture.getTarget())
                                .status(lecture.isStatus())
                                .longAddress(lecture.getAddress())
                                .shortAddress(shortAddress)
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
                throw new CustomException(Code.EMAIL_NOT_EXIST);
            }

            log.info("readLectures: member email {}", member.getAccountEmail());
            Slice<Lecture> lectures = lectureRepository.findAllByOrderByStatusDescViewDesc(pageable);
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
                                .division(lecture.getDivision())
                                .latitude(lecture.getLatitude())
                                .longitude(lecture.getLongitude())
                                .hostedBy(lecture.getCenterName())
                                .dayOfWeek(lecture.getDayOfWeek())
                                .target(lecture.getTarget())
                                .status(lecture.isStatus())
                                .longAddress(lecture.getAddress())
                                .shortAddress(shortAddress)
                                .link(lecture.getLink())
                                .heart(isHeart).build();
                    }).toList();

            return new SliceImpl<>(lectureHomeResponseList, pageable, lectures.hasNext());
        }
    }

    public Slice<LectureHomeResponse> readLectures(String token, Pageable pageable) {
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            log.info("readLectues -> 회원이 아닙니다.");
            Slice<Lecture> lectures = lectureRepository.findAllByOrderByStatusDesc(pageable);
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
                                .division(lecture.getDivision())
                                .startDate(lecture.getStartDate())
                                .endDate(lecture.getEndDate())
                                .latitude(lecture.getLatitude())
                                .longitude(lecture.getLongitude())
                                .hostedBy(lecture.getCenterName())
                                .dayOfWeek(lecture.getDayOfWeek())
                                .longAddress(lecture.getAddress())
                                .shortAddress(shortAddress)
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
                throw new CustomException(Code.EMAIL_NOT_EXIST);
            }

            log.info("readLectures: member email {}", member.getAccountEmail());
            Slice<Lecture> lectures = lectureRepository.findAllByOrderByStatusDesc(pageable);
            List<LectureHomeResponse> lectureHomeResponseList = lectures.getContent().stream()
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

    public LectureDetailResponse readLecture(String token, Integer id) {

        Lecture lecture = lectureRepository
                .findById(id)
                .orElseThrow(() -> new CustomException(Code.LECTURE_ID_NOT_EXIST));

        lecture.setView(lecture.getView() + 1);  // 조회수 증가
        lecture = lectureRepository.save(lecture);
        // 회원이 아닌 경우 -> 찜 정보가 노출이 되지 않는다.
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            log.info("회원이 아닙니다.");
//            dist = Math.round(dist * 10.0) / 10.0;
//            String distance = String.valueOf(dist) + "m";
//            String estimatedTime = "도보 약 " + String.valueOf((int) (dist * 0.02)) + "분 이내";
//            log.info("dist: {}", dist);

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
            long dDay;
            if (lastDate == null) {
                lastDate = LocalDate.parse(lecture.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            }
            dDay = ChronoUnit.DAYS.between(curDate, lastDate);
            log.info("lastDate: {} / curDate: {} / dDay: {}", lastDate, curDate, dDay);
            if (dDay < 0) {
                log.info("마감입니다. dDay: {}", dDay);
                lecture.setStatus(false); // D-day가 끝났으면 마감처리
            }

            Map<Integer, String> plan = new HashMap<>();
            List<Educate> educateList = educateRepository.findByLectureId(lecture.getId());
            for (int i = 1; i <= educateList.size(); i++) {
                log.info("educateList: {}", educateList.get(i - 1).getContent());
                plan.put(i, String.valueOf(educateList.get(i - 1).getContent()));
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
                    .latitude(lecture.getLatitude())
                    .longitude(lecture.getLongitude())
                    .category("미정")
                    .dDay(-dDay)
                    .plan(plan)
                    .detail(lecture.getDescription())
                    .certification(lecture.getCertification())
                    .textBookName(lecture.getTextBookName())
                    .textBookPrice(lecture.getTextBookPrice())
                    .instructorName(teacherInfoList)
                    .need(lecture.getNeed())
                    .images(lecture.getImageUrls())
                    .heart(false)
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
                throw new CustomException(Code.EMAIL_NOT_EXIST);
            }

//            double dist = calculateDistance(latitude, longitude, lecture.getLatitude(), lecture.getLongitude()) * 1000;
//            dist = Math.round(dist * 10.0) / 10.0;
//            String distance = String.valueOf(dist) + "m";
//            String estimatedTime = "도보 약 " + String.valueOf((int) (dist * 0.02)) + "분 이내";

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
            long dDay;
            if (lastDate == null) {
                lastDate = LocalDate.parse(lecture.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            }
            dDay = ChronoUnit.DAYS.between(curDate, lastDate);
            log.info("lastDate: {} / curDate: {} / dDay: {}", lastDate, curDate, dDay);
            if (dDay < 0) {
                log.info("마감입니다. dDay: {}", dDay);
                lecture.setStatus(false); // D-day가 끝났으면 마감처리
            }

            Map<Integer, String> plan = new HashMap<>();
            List<Educate> educateList = educateRepository.findByLectureId(lecture.getId());
            for (int i = 1; i <= educateList.size(); i++) {
                log.info("educateList: {}", educateList.get(i - 1).getContent());
                plan.put(i, String.valueOf(educateList.get(i - 1).getContent()));
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
                    .latitude(lecture.getLatitude())
                    .longitude(lecture.getLongitude())
                    .hostedBy(lecture.getCenterName())
                    .thumbnail(lecture.getThumbnail())
                    .address(lecture.getAddress())
                    .division(lecture.getDivision())
                    .plan(plan)
                    .condition(lecture.getTarget())
                    .dDay(-dDay)
                    .detail(lecture.getDescription())
                    .certification(lecture.getCertification())
                    .category("미정")
                    .textBookName(lecture.getTextBookName())
                    .textBookPrice(lecture.getTextBookPrice())
                    .instructorName(teacherInfoList)
                    .images(lecture.getImageUrls())
                    .need(lecture.getNeed())
                    .heart(heartService.isHearted(id, member.getId()))
                    .build();

            return lectureDetailResponse;
        }
    }

    public List<PickHomeResponse> getPickClasses(String token) {
        // 회원이 아닌 경우
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {

            List<Lecture> topLectures = lectureRepository.findTop6ByStatusTrueOrderByViewDesc();

            return topLectures.stream()
                    .map(lecture -> {
                        String[] addressList = lecture.getAddress().split(" ");
                        String shortAddress = addressList[0] + " " + addressList[1];
                        return PickHomeResponse.builder()
                                .id(lecture.getId())
                                .view(lecture.getView())
                                .name(lecture.getName())
                                .longAddress(lecture.getAddress())
                                .shortAddress(shortAddress)
                                .time(lecture.getTime())
                                .division(lecture.getDivision())
                                .thumbnail(lecture.getThumbnail())
                                .startDate(lecture.getStartDate())
                                .endDate(lecture.getEndDate())
                                .dayOfWeek(lecture.getDayOfWeek())
                                .status(lecture.isStatus())
                                .target(lecture.getTarget())
                                .link(lecture.getLink())
                                .heart(false)
                                .build();
                    }).collect(Collectors.toList());

        }
        // 회원인 경우
        else {
            String jwtToken = token.substring(7);
            Claims claims = jwtTokenProvider.parseClaims(jwtToken);
            log.info("readLectures: token not null");

            Member member = memberRepository.findByAccountEmail(claims.getSubject());
            if (member == null) {
                throw new CustomException(Code.EMAIL_NOT_EXIST);
            }

            List<Lecture> topLectures = lectureRepository.findTop6ByStatusTrueOrderByViewDesc();

            return topLectures.stream()
                    .map(lecture -> {
                        boolean isHeart = heartService.isHearted(lecture.getId(), member.getId());
                        String[] addressList = lecture.getAddress().split(" ");
                        String shortAddress = addressList[0] + " " + addressList[1];
                        return PickHomeResponse.builder()
                                .id(lecture.getId())
                                .view(lecture.getView())
                                .name(lecture.getName())
                                .longAddress(lecture.getAddress())
                                .shortAddress(shortAddress)
                                .time(lecture.getTime())
                                .division(lecture.getDivision())
                                .thumbnail(lecture.getThumbnail())
                                .startDate(lecture.getStartDate())
                                .endDate(lecture.getEndDate())
                                .dayOfWeek(lecture.getDayOfWeek())
                                .status(lecture.isStatus())
                                .target(lecture.getTarget())
                                .link(lecture.getLink())
                                .heart(isHeart)
                                .build();
                    }).collect(Collectors.toList());
        }
    }

    public Slice<LectureHomeResponse> readLectureByLocation(String token, String loc, Pageable pageable) {
        // 회원이 아닌 경우
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            loc = loc.replace("\"", " ").replace("\'", " ").trim();
            Slice<Lecture> lectures = lectureRepository.findLecturesByLocation(loc, pageable);
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
                                .division(lecture.getDivision())
                                .startDate(lecture.getStartDate())
                                .endDate(lecture.getEndDate())
                                .latitude(lecture.getLatitude())
                                .longitude(lecture.getLongitude())
                                .hostedBy(lecture.getCenterName())
                                .dayOfWeek(lecture.getDayOfWeek())
                                .longAddress(lecture.getAddress())
                                .shortAddress(shortAddress)
                                .status(lecture.isStatus())
                                .link(lecture.getLink())
                                .heart(false).build();
                    }).toList();
            return new SliceImpl<>(lectureHomeResponseList, pageable, lectures.hasNext());
        }
        // 회원이 아닌 경우
        else {
            log.info("readLectures -> 회원입니다.");
            String jwtToken = token.substring(7);
            Claims claims = jwtTokenProvider.parseClaims(jwtToken);
            log.info("readLectures: token not null");

            Member member = memberRepository.findByAccountEmail(claims.getSubject());
            if (member == null) {
                throw new CustomException(Code.EMAIL_NOT_EXIST);
            }

            log.info("readLectures: member email {}", member.getAccountEmail());
            loc = loc.replace("\"", " ").replace("\'", " ").trim();
            Slice<Lecture> lectures = lectureRepository.findLecturesByLocation(loc, pageable);
            List<LectureHomeResponse> lectureHomeResponseList = lectures.getContent().stream()
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

    public List<MarkerResponse> getMarkerClass() {
        List<Object[]> lectures = lectureRepository.findGroupedLecturesByLocation();
        List<MarkerResponse> markerResponseList = lectures.stream()
                .map(lecture -> {
                    String address = (String) lecture[0]; // address
                    Double latitude = (Double) lecture[1]; // latitude
                    Double longitude = (Double) lecture[2]; // longitude
                    String centerName = (String) lecture[3]; // centerName

                    String[] addressList = address.split(" ");
                    String shortAddress = addressList[0] + " " + addressList[1];

                    return MarkerResponse.builder()
                            .longAddress(address)
                            .shortAddress(shortAddress)
                            .hostedBy(centerName)
                            .longitude(longitude)
                            .latitude(latitude).build();
                }).toList();

        return markerResponseList;
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
