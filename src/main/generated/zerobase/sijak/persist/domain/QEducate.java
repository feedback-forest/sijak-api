package zerobase.sijak.persist.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QEducate is a Querydsl query type for Educate
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEducate extends EntityPathBase<Educate> {

    private static final long serialVersionUID = 139399258L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QEducate educate = new QEducate("educate");

    public final StringPath content = createString("content");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final QLecture lecture;

    public QEducate(String variable) {
        this(Educate.class, forVariable(variable), INITS);
    }

    public QEducate(Path<? extends Educate> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QEducate(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QEducate(PathMetadata metadata, PathInits inits) {
        this(Educate.class, metadata, inits);
    }

    public QEducate(Class<? extends Educate> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.lecture = inits.isInitialized("lecture") ? new QLecture(forProperty("lecture")) : null;
    }

}

