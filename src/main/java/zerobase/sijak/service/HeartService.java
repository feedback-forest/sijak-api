package zerobase.sijak.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zerobase.sijak.dto.LectureHomeResponse;
import zerobase.sijak.exception.*;
import zerobase.sijak.jwt.JwtTokenProvider;
import zerobase.sijak.persist.domain.Heart;
import zerobase.sijak.persist.domain.Lecture;
import zerobase.sijak.persist.domain.Member;
import zerobase.sijak.persist.repository.HeartRepository;
import zerobase.sijak.persist.repository.LectureRepository;
import zerobase.sijak.persist.repository.MemberRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HeartService {

    private final HeartRepository heartRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final LectureRepository lectureRepository;


    public boolean isHearted(int lectureId, int memberId) {
        return heartRepository.existsByLectureIdAndMemberId(lectureId, memberId);
    }

    public Page<LectureHomeResponse> readHearts(String token, boolean mode, Pageable pageable) {
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            throw new CustomException(Code.EMAIL_NOT_EXIST);
        }
        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("email : {}", claims.getSubject());

        Member member = memberRepository.findByAccountEmail(claims.getSubject());
        if (member == null) throw new CustomException(Code.EMAIL_NOT_EXIST);

        Page<Lecture> lectures = heartRepository.findLecturesByMemberIdOrderByStatus(member.getId(), pageable);

        List<LectureHomeResponse> lecturesResponse = lectures.getContent().stream()
                .filter(lecture -> !mode || lecture.isStatus())
                .map(lecture -> {
                    boolean isHeart = isHearted(lecture.getId(), member.getId());
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
                            .hostedBy(lecture.getCenterName())
                            .dayOfWeek(lecture.getDayOfWeek())
                            .target(lecture.getTarget())
                            .status(lecture.isStatus())
                            .longAddress(lecture.getAddress())
                            .shortAddress(shortAddress)
                            .link(lecture.getLink())
                            .heart(isHeart).build();
                }).toList();

        long totalElements = heartRepository.countByLectureIdAndMemberIdAndMode(member.getId(), mode);
        log.info("totalElements: {}", totalElements);
        return new PageImpl<>(lecturesResponse, pageable, totalElements);
    }

    public void appendHeart(String token, int lectureId) {
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            throw new CustomException(Code.EMAIL_NOT_EXIST);
        }
        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("email : {}", claims.getSubject());

        Member member = memberRepository.findByAccountEmail(claims.getSubject());
        if (member == null) throw new CustomException(Code.EMAIL_NOT_EXIST);

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new CustomException(Code.LECTURE_ID_NOT_EXIST));

        if (heartRepository.existsByLectureIdAndMemberId(lectureId, member.getId())) {
            throw new CustomException(Code.ALREADY_PUSH_HEART);
        }

        Heart heart = Heart.builder()
                .member(member)
                .lecture(lecture).build();

        heartRepository.save(heart);
    }

    public void deleteHeart(String token, int lectureId) {
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            throw new CustomException(Code.EMAIL_NOT_EXIST);
        }
        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("email : {}", claims.getSubject());

        Member member = memberRepository.findByAccountEmail(claims.getSubject());
        if (member == null) throw new CustomException(Code.EMAIL_NOT_EXIST);

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new CustomException(Code.LECTURE_ID_NOT_EXIST));

        Heart heart = heartRepository.findByLectureIdAndMemberId(lectureId, member.getId());

        if (heart == null) throw new CustomException(Code.HEART_REMOVE_FAILED);

        heartRepository.delete(heart);
    }

    public void deleteDeactivatedHearts(String token) {

        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            throw new CustomException(Code.EMAIL_NOT_EXIST);
        }
        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("email : {}", claims.getSubject());

        Member member = memberRepository.findByAccountEmail(claims.getSubject());
        if (member == null) throw new CustomException(Code.EMAIL_NOT_EXIST);

        heartRepository.deleteClosedLecturesFromHearts(member.getId());
    }

}
