package zerobase.sijak.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
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

import java.util.ArrayList;
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

    public Slice<LectureHomeResponse> readHearts(String token, Pageable pageable) {
        if (token == null || token.isEmpty() || token.trim().equals("Bearer")) {
            throw new EmailNotExistException("해당 유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);
        }
        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("email : {}", claims.getSubject());

        Member member = memberRepository.findByAccountEmail(claims.getSubject());
        if (member == null) throw new EmailNotExistException("해당 유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);

        Slice<Lecture> lectures = heartRepository.findLecturesByMemberId(member.getId(), pageable);

        List<LectureHomeResponse> lecturesResponse = lectures.getContent().stream()
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
                            .dayOfWeek(lecture.getDayOfWeek())
                            .target(lecture.getTarget())
                            .status(lecture.isStatus())
                            .address(shortAddress)
                            .link(lecture.getLink())
                            .heart(isHeart).build();
                }).toList();

        return new SliceImpl<>(lecturesResponse, pageable, lectures.hasNext());
    }

    public void appendHeart(String token, int lectureId) {
        if (token == null || token.isEmpty()) {
            throw new EmailNotExistException("해당 유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);
        }
        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("email : {}", claims.getSubject());

        Member member = memberRepository.findByAccountEmail(claims.getSubject());
        if (member == null) throw new EmailNotExistException("해당 유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IdNotExistException("해당 강의 id가 존재하지 않습니다.", ErrorCode.LECTURE_ID_NOT_EXIST));

        if (heartRepository.existsByLectureIdAndMemberId(lectureId, member.getId())) {
            throw new AlreadyHeartException("이미 찜한 강의입니다.", ErrorCode.ALREADY_PUSH_HEART);
        }

        Heart heart = Heart.builder()
                .member(member)
                .lecture(lecture).build();

        heartRepository.save(heart);
    }

    public void deleteHeart(String token, int lectureId) {
        if (token == null || token.isEmpty()) {
            throw new EmailNotExistException("해당 유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);
        }
        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("email : {}", claims.getSubject());

        Member member = memberRepository.findByAccountEmail(claims.getSubject());
        if (member == null) throw new EmailNotExistException("해당 유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IdNotExistException("해당 강의 id가 존재하지 않습니다.", ErrorCode.LECTURE_ID_NOT_EXIST));

        Heart heart = heartRepository.findByLectureIdAndMemberId(lectureId, member.getId());

        if (heart == null) throw new HeartRemoveException("찜클래스 삭제에 실패했습니다.", ErrorCode.HEART_REMOVE_FAILED);

        heartRepository.delete(heart);
    }

}
