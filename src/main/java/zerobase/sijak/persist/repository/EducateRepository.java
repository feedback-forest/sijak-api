package zerobase.sijak.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.sijak.persist.domain.Educate;

import java.util.List;

@Repository
public interface EducateRepository extends JpaRepository<Educate, Integer> {

    List<Educate> findByLectureId(int lectureId);
}
