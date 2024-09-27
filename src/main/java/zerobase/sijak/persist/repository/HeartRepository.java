package zerobase.sijak.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.sijak.persist.domain.Heart;
import zerobase.sijak.persist.domain.Lecture;

import java.util.List;

@Repository
public interface HeartRepository extends JpaRepository<Heart, Integer> {

    boolean existsByLectureIdAndMemberId(Integer lectureId, Integer memberId);

    List<Heart> findAllByMemberId(Integer memberId);

}
