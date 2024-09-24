package zerobase.sijak.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.sijak.persist.domain.Teacher;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
}
