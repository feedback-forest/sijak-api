package zerobase.sijak.persist.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 234214309L;

    public static final QMember member = new QMember("member1");

    public final StringPath accountEmail = createString("accountEmail");

    public final StringPath address = createString("address");

    public final StringPath ageRange = createString("ageRange");

    public final StringPath birth = createString("birth");

    public final StringPath gender = createString("gender");

    public final ListPath<Heart, QHeart> hearts = this.<Heart, QHeart>createList("hearts", Heart.class, QHeart.class, PathInits.DIRECT2);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath kakaoUserId = createString("kakaoUserId");

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final StringPath name = createString("name");

    public final StringPath phoneNumber = createString("phoneNumber");

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final StringPath profileNickname = createString("profileNickname");

    public final ListPath<Review, QReview> reviews = this.<Review, QReview>createList("reviews", Review.class, QReview.class, PathInits.DIRECT2);

    public final ListPath<Term, QTerm> terms = this.<Term, QTerm>createList("terms", Term.class, QTerm.class, PathInits.DIRECT2);

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

