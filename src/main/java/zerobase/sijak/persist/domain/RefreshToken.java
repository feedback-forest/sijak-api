package zerobase.sijak.persist.domain;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 4)
public class RefreshToken {

    @Id
    private String key;

    @Indexed
    private String value;

    @Builder
    public RefreshToken(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public RefreshToken updateValue(String token) {
        this.value = token;
        return this;
    }

    public String getKakaoUserID() {
        return key;
    }

    public String getRefreshToken() {
        return value;
    }

}
