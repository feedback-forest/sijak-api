package zerobase.sijak.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.sijak.persist.domain.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
}
