package zerobase.sijak.persist.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import zerobase.sijak.persist.domain.Lecture;

import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Integer> {

    Lecture findByName(String name);

    Lecture findByLink(String link);

    List<Lecture> findTop6ByOrderByViewDesc();

    Slice<Lecture> findAllBy(Pageable pageable);

    @Query(value = "SELECT * FROM lecture l WHERE " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(l.latitude)) * " +
            "cos(radians(l.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
            "sin(radians(l.latitude)))) < :distance",
            countQuery = "SELECT count(*) FROM lecture l WHERE " +
                    "(6371 * acos(cos(radians(:latitude)) * cos(radians(l.latitude)) * " +
                    "cos(radians(l.longitude) - radians(:longitude)) + sin(radians(:latitude)) * " +
                    "sin(radians(l.latitude)))) < :distance",
            nativeQuery = true)
    Slice<Lecture> findLecturesByDistance(@Param("latitude") double latitude,
                                          @Param("longitude") double longitude,
                                          @Param("distance") double distance,
                                          Pageable pageable);

}
