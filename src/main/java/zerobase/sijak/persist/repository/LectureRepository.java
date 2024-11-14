package zerobase.sijak.persist.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import zerobase.sijak.persist.domain.Lecture;

import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Integer>, LectureQueryDslRepository {

    Lecture findByLink(String link);

    Slice<Lecture> findAllByOrderByStatusDescViewDesc(Pageable pageable);

    Slice<Lecture> findAllByOrderByStatusDesc(Pageable pageable);

    List<Lecture> findTop6ByStatusTrueOrderByViewDesc();

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

    @Query("SELECT l " +
            "FROM Lecture l " +
            "WHERE (:location IS NULL OR :location = '' OR l.address LIKE %:location%) " +
            "ORDER BY l.status DESC")
    Slice<Lecture> findLecturesByLocation(@Param("location") String location, Pageable pageable);


    @Query("SELECT l.address, l.latitude, l.longitude, l.centerName " +
            "FROM Lecture l " +
            "GROUP BY l.address, l.latitude, l.longitude, l.centerName ")
    List<Object[]> findGroupedLecturesByLocation();

    List<Lecture> findAllByStatusTrue();

    boolean existsByNameAndCenterNameAndStartDate(String name, String centerName, String startDate);

    @Modifying
    @Query("UPDATE Lecture l SET l.status = false WHERE l.endDate < :date AND l.status = true AND l.name = :centerName")
    void updateStatusToFalseForExpiredClasses(String date, String centerName);


}
