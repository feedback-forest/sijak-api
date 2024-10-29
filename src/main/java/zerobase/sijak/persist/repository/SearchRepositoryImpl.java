package zerobase.sijak.persist.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SearchRepositoryImpl implements SearchQueryDslRepository {

    private final JPAQueryFactory queryFactory;

}
