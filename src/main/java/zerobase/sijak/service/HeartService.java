package zerobase.sijak.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import zerobase.sijak.exception.EmailNotExistException;
import zerobase.sijak.exception.ErrorCode;
import zerobase.sijak.jwt.JwtTokenProvider;
import zerobase.sijak.persist.domain.Heart;
import zerobase.sijak.persist.domain.Lecture;
import zerobase.sijak.persist.domain.Member;
import zerobase.sijak.persist.repository.HeartRepository;
import zerobase.sijak.persist.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HeartService {

    private final HeartRepository heartRepository;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;


    public boolean isHearted(int lectureId, int memberId) {
        return heartRepository.existsByLectureIdAndMemberId(lectureId, memberId);
    }

    public List<Lecture> readHearts(String token) {
        String jwtToken = token.substring(7);
        Claims claims = jwtTokenProvider.parseClaims(jwtToken);
        log.info("email : {}", claims.getSubject());

        Member member = memberRepository.findByAccountEmail(claims.getSubject());
        if (member == null) throw new EmailNotExistException("해당 유저 email이 존재하지 않습니다.", ErrorCode.EMAIL_NOT_EXIST);

        List<Heart> hearts = heartRepository.findAllByMemberId(member.getId());
        return hearts.stream()
                .map(Heart::getLecture)
                .collect(Collectors.toList());

    }

    public void appendHearts(String token) {

    }

}
