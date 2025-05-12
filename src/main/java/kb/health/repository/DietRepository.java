package kb.health.repository;

import jakarta.persistence.EntityManager;
import kb.health.domain.record.Diet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DietRepository {

    private final EntityManager em;

    // 음식 여러 개 등록
    public void saveAll(List<Diet> diets) {
        for (Diet diet : diets) {
            em.persist(diet);
        }
    }

    // 음식 등록
    public Long save(Diet diet) {
        em.persist(diet);

        return diet.getId();
    }

    // 음식 단건 조회
    public Diet findById(Long id) {
        return em.find(Diet.class, id);
    }

    // 음식 전체 목록 조회
    public List<Diet> findAll() {
        return em.createQuery("SELECT d FROM Diet d", Diet.class)
                .getResultList();
    }

    // 음식 삭제
    public void delete(Long id) {
        Diet diet = em.find(Diet.class, id);
        if (diet != null) {
            em.remove(diet);
        }
    }

    // 메뉴 키워드로 음식 검색
    public List<Diet> findByMenuKeyword(String keyword) {
        return em.createQuery(
                        "SELECT d FROM Diet d WHERE d.menu LIKE :keyword", Diet.class)
                .setParameter("keyword", "%" + keyword + "%")
                .getResultList();
    }

}
