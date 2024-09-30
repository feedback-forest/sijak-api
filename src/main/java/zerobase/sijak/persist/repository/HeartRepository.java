package zerobase.sijak.persist.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import zerobase.sijak.persist.domain.Heart;
import zerobase.sijak.persist.domain.Lecture;

import java.util.List;

@Repository
public interface HeartRepository extends JpaRepository<Heart, Integer> {

    boolean existsByLectureIdAndMemberId(Integer lectureId, Integer memberId);

    List<Heart> findAllByMemberId(Integer memberId);

    Heart findByLectureIdAndMemberId(Integer lectureId, Integer memberId);

    @Query("SELECT h.lecture FROM Heart h WHERE h.member.id = :memberId")
    Slice<Lecture> findLecturesByMemberId(@Param("memberId") Integer memberId, Pageable pageable);

}
