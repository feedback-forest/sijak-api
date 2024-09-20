package zerobase.sijak.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.sijak.jwt.KakaoUser;
import java.util.Optional;

@Repository
public interface KakaoRepository extends JpaRepository<KakaoUser, Long> {
    Optional<KakaoUser> findByEmail(String email);
}
