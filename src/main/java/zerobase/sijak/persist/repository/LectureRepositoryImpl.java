package zerobase.sijak.persist.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;
import zerobase.sijak.persist.domain.Lecture;
import zerobase.sijak.persist.domain.QLecture;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LectureRepositoryImpl implements LectureQueryDslRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<Lecture> searchKeywordAndLocation(Pageable pageable, String keyword, String location) {

        QLecture lecture = QLecture.lecture;

        List<Lecture> lectures = jpaQueryFactory.selectFrom(lecture)
                .where(containsKeyword(keyword), eqLocation(location), eqStatusTrue())
                .orderBy(lecture.deadline.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = lectures.size() > pageable.getPageSize();

        if (hasNext) {
            lectures.remove(lectures.size() - 1); // 다음 페이지 확인용으로 가져온 추가 데이터 제거
        }

        return new SliceImpl<>(lectures, pageable, hasNext);
    }

    // 제목과 설명에 해당 keyword 가 포함되어있는 강좌를 모두 찾는다.
    private BooleanExpression containsKeyword(String keyword) {
        if (StringUtils.isBlank(keyword)) return null;
        return QLecture.lecture.name.contains(keyword)
                .or(QLecture.lecture.description.contains(keyword));
    }

    // 해당 location 을 포함하는 address 를 가진 강좌를 모두 찾는다.
    private BooleanExpression eqLocation(String location) {
        if (StringUtils.isBlank(location)) return null;
        return QLecture.lecture.address.contains(location);
    }

    // 활성화된 강좌를 모두 찾는다.
    private BooleanExpression eqStatusTrue() {
        return QLecture.lecture.status.eq(true);
    }

}
