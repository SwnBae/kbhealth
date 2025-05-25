package kb.health.repository.record;

import kb.health.domain.record.Diet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DietRepository extends JpaRepository<Diet, Long> {

    // 메뉴 키워드로 음식 검색
    List<Diet> findByMenuContaining(String keyword);
}
