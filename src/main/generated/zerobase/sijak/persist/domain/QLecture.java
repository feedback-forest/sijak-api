package zerobase.sijak.persist.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLecture is a Querydsl query type for Lecture
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLecture extends EntityPathBase<Lecture> {

    private static final long serialVersionUID = 2069489107L;

    public static final QLecture lecture = new QLecture("lecture");

    public final StringPath address = createString("address");

    public final NumberPath<Integer> capacity = createNumber("capacity", Integer.class);

    public final StringPath category = createString("category");

    public final StringPath centerName = createString("centerName");

    public final StringPath certification = createString("certification");

    public final StringPath dayOfWeek = createString("dayOfWeek");

    public final DateTimePath<java.time.LocalDateTime> deadline = createDateTime("deadline", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final StringPath division = createString("division");

    public final ListPath<Educate, QEducate> educates = this.<Educate, QEducate>createList("educates", Educate.class, QEducate.class, PathInits.DIRECT2);

    public final StringPath endDate = createString("endDate");

    public final ListPath<Heart, QHeart> hearts = this.<Heart, QHeart>createList("hearts", Heart.class, QHeart.class, PathInits.DIRECT2);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final ListPath<Image, QImage> imageUrls = this.<Image, QImage>createList("imageUrls", Image.class, QImage.class, PathInits.DIRECT2);

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final StringPath link = createString("link");

    public final StringPath location = createString("location");

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final StringPath name = createString("name");

    public final StringPath need = createString("need");

    public final StringPath price = createString("price");

    public final ListPath<Review, QReview> reviews = this.<Review, QReview>createList("reviews", Review.class, QReview.class, PathInits.DIRECT2);

    public final StringPath startDate = createString("startDate");

    public final BooleanPath status = createBoolean("status");

    public final StringPath target = createString("target");

    public final ListPath<Teacher, QTeacher> teachers = this.<Teacher, QTeacher>createList("teachers", Teacher.class, QTeacher.class, PathInits.DIRECT2);

    public final StringPath tel = createString("tel");

    public final StringPath textBookName = createString("textBookName");

    public final StringPath textBookPrice = createString("textBookPrice");

    public final StringPath thumbnail = createString("thumbnail");

    public final StringPath time = createString("time");

    public final NumberPath<Integer> total = createNumber("total", Integer.class);

    public final NumberPath<Integer> view = createNumber("view", Integer.class);

    public QLecture(String variable) {
        super(Lecture.class, forVariable(variable));
    }

    public QLecture(Path<? extends Lecture> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLecture(PathMetadata metadata) {
        super(Lecture.class, metadata);
    }

}

