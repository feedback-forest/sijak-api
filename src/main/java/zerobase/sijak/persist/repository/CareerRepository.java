package zerobase.sijak.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.sijak.persist.domain.Career;

@Repository
public interface CareerRepository extends JpaRepository<Career, Integer> {
}
