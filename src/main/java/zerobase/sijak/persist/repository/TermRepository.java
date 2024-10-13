package zerobase.sijak.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.sijak.persist.domain.Term;

import java.util.List;

@Repository
public interface TermRepository extends JpaRepository<Term, Integer> {

    List<Term> findByMemberId(Integer memberId);
}
