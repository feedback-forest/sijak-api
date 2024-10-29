package zerobase.sijak.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.sijak.persist.domain.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {

    Optional<Member> findByKakaoUserId(String kakaoUserId);

    Optional<Member> findByIdAndKakaoUserId(Integer id, String kakaoUserId);

    boolean existsByProfileNickname(String profileNickname);

}
