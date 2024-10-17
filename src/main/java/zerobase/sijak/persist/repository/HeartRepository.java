package zerobase.sijak.persist.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zerobase.sijak.persist.domain.Heart;
import zerobase.sijak.persist.domain.Lecture;

import java.util.List;

@Repository
public interface HeartRepository extends JpaRepository<Heart, Integer> {

    boolean existsByLectureIdAndMemberId(Integer lectureId, Integer memberId);

    Heart findByLectureIdAndMemberId(Integer lectureId, Integer memberId);

    @Query("SELECT h.lecture FROM Heart h WHERE h.member.id = :memberId")
    Slice<Lecture> findLecturesByMemberId(@Param("memberId") Integer memberId, Pageable pageable);

    @Query("SELECT l FROM Lecture l " +
            "JOIN Heart h ON h.lecture.id = l.id " +
            "WHERE h.member.id = :memberId " +
            "ORDER BY l.status DESC")
    Page<Lecture> findLecturesByMemberIdOrderByStatus(@Param("memberId") Integer memberId, Pageable pageable);


    @Query("SELECT COUNT(l) FROM Lecture l " +
            "JOIN Heart h ON h.lecture.id = l.id " +
            "WHERE h.member.id = :memberId " +
            "AND (:mode = false OR l.status = true)")
    long countByLectureIdAndMemberIdAndMode(@Param("memberId") Integer memberId, @Param("mode") boolean mode);

    @Modifying
    @Transactional
    @Query("DELETE FROM Heart h " +
            "WHERE h.member.id = :memberId " +
            "AND h.lecture.id IN (" +
            "    SELECT l.id FROM Lecture l WHERE l.status = false" +
            ")")
    void deleteClosedLecturesFromHearts(@Param("memberId") Integer memberId);
}
