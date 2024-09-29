package zerobase.sijak.persist.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.sijak.persist.domain.Lecture;

import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Integer> {

    Lecture findByName(String name);

    Lecture findByLink(String link);

    List<Lecture> findTop6ByOrderByViewDesc();

    Slice<Lecture> findAllBy(Pageable pageable);

}
