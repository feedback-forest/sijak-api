package zerobase.sijak.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import zerobase.sijak.dto.LectureHomeResponse;
import zerobase.sijak.exception.EmailNotExistException;
import zerobase.sijak.exception.ErrorCode;
import zerobase.sijak.exception.IdNotExistException;
import zerobase.sijak.jwt.JwtTokenProvider;
import zerobase.sijak.persist.domain.Heart;
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


    public Slice<LectureHomeResponse> readLectures(String token, Pageable pageable) {

        if (token == null || token.isEmpty()) {
            log.info("readLectues -> 회원이 아닙니다.");
            Slice<Lecture> lectures = lectureRepository.findAll(pageable);
            List<LectureHomeResponse> lectureHomeResponseList = lectures.getContent().stream().map(lecture -> {
                String[] addressList = lecture.getAddress().split(" ");
                String shortAddress = addressList[0] + " " + addressList[1];
                return LectureHomeResponse.builder()
                        .id(lecture.getId())
                        .name(lecture.getName())
                        .time(lecture.getTime())
                        .target(lecture.getTarget())
                        .address(shortAddress)
                        .link(lecture.getLink())
                        .isHeart(false).build();
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
            List<LectureHomeResponse> lectureHomeResponseList = lectures.getContent().stream().map(lecture -> {
                boolean isHeart = heartService.isHearted(lecture.getId(), member.getId());
                String[] addressList = lecture.getAddress().split(" ");
                String shortAddress = addressList[0] + " " + addressList[1];
                return LectureHomeResponse.builder()
                        .id(lecture.getId())
                        .name(lecture.getName())
                        .time(lecture.getTime())
                        .target(lecture.getTarget())
                        .address(shortAddress)
                        .link(lecture.getLink())
                        .isHeart(isHeart).build();
            }).toList();

            return new SliceImpl<>(lectureHomeResponseList, pageable, lectures.hasNext());
        }
    }

    public Lecture readLecture(Integer id) {

        Lecture lecture = lectureRepository
                .findById(id)
                .orElseThrow(() -> new IdNotExistException("해당 강의 id가 존재하지 않습니다.", ErrorCode.LECTURE_ID_NOT_EXIST));

        lecture.setView(lecture.getView() + 1);
        lectureRepository.save(lecture);

        return lecture;
    }



}
