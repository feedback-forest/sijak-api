package zerobase.sijak.jwt;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QKakaoUser is a Querydsl query type for KakaoUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QKakaoUser extends EntityPathBase<KakaoUser> {

    private static final long serialVersionUID = 1825283266L;

    public static final QKakaoUser kakaoUser = new QKakaoUser("kakaoUser");

    public final StringPath ageRange = createString("ageRange");

    public final StringPath birth = createString("birth");

    public final StringPath email = createString("email");

    public final StringPath gender = createString("gender");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> kakaoUserId = createNumber("kakaoUserId", Long.class);

    public final StringPath name = createString("name");

    public final StringPath nickname = createString("nickname");

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final ListPath<String, StringPath> roles = this.<String, StringPath>createList("roles", String.class, StringPath.class, PathInits.DIRECT2);

    public final EnumPath<zerobase.sijak.dto.SocialType> socialType = createEnum("socialType", zerobase.sijak.dto.SocialType.class);

    public QKakaoUser(String variable) {
        super(KakaoUser.class, forVariable(variable));
    }

    public QKakaoUser(Path<? extends KakaoUser> path) {
        super(path.getType(), path.getMetadata());
    }

    public QKakaoUser(PathMetadata metadata) {
        super(KakaoUser.class, metadata);
    }

}

