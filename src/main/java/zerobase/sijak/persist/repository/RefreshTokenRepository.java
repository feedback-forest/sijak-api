package zerobase.sijak.persist.repository;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;
import zerobase.sijak.persist.domain.RefreshToken;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class RefreshTokenRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenRepository(final RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(final RefreshToken refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(refreshToken.getKakaoUserID(), refreshToken.getRefreshToken());
        redisTemplate.expire(refreshToken.getKakaoUserID(), 30, TimeUnit.SECONDS);
    }

    public Optional<RefreshToken> findById(final String refreshToken) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String userId = valueOperations.get(refreshToken);

        if (Objects.isNull(userId)) {
            return Optional.empty();
        }

        return Optional.of(new RefreshToken(refreshToken, userId));
    }

    public void delete(final String userId) {
        redisTemplate.delete(userId);
    }

}
