package zerobase.sijak.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.sijak.persist.domain.Lecture;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Integer> {
}
