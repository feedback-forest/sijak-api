package zerobase.sijak.persist.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;
import zerobase.sijak.persist.domain.Lecture;

@Repository
public interface LectureQueryDslRepository {

    Slice<Lecture> searchKeywordAndLocation(Pageable pageable, String keyword, String location);

    Slice<Lecture> searchCategoryAndLocation(Pageable pageable, String category, String location);
}
