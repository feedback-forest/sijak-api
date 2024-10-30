package zerobase.sijak.persist.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeacher is a Querydsl query type for Teacher
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeacher extends EntityPathBase<Teacher> {

    private static final long serialVersionUID = 577217591L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeacher teacher = new QTeacher("teacher");

    public final ListPath<Career, QCareer> careers = this.<Career, QCareer>createList("careers", Career.class, QCareer.class, PathInits.DIRECT2);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final QLecture lecture;

    public final StringPath name = createString("name");

    public QTeacher(String variable) {
        this(Teacher.class, forVariable(variable), INITS);
    }

    public QTeacher(Path<? extends Teacher> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeacher(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeacher(PathMetadata metadata, PathInits inits) {
        this(Teacher.class, metadata, inits);
    }

    public QTeacher(Class<? extends Teacher> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.lecture = inits.isInitialized("lecture") ? new QLecture(forProperty("lecture")) : null;
    }

}

