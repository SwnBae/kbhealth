package kb.health.Repository;

import jakarta.persistence.EntityManager;
import kb.health.domain.record.DietRecord;
import kb.health.domain.record.ExerciseRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecordRepository {

    private final EntityManager em;

    // 식단 레코드 저장
    public void saveDietRecord(DietRecord dietRecord) {
        em.persist(dietRecord);
    }

    // 운동 레코드 저장
    public void saveExerciseRecord(ExerciseRecord exerciseRecord) {
        em.persist(exerciseRecord);
    }

    // 특정 Member의 모든 식단 기록 조회
    public List<DietRecord> findDietRecordsByMember(Long memberId) {
        return em.createQuery("SELECT d FROM DietRecord d WHERE d.member.id = :memberId", DietRecord.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    // 특정 Member의 모든 운동 기록 조회
    public List<ExerciseRecord> findExerciseRecordsByMember(Long memberId) {
        return em.createQuery("SELECT e FROM ExerciseRecord e WHERE e.member.id = :memberId", ExerciseRecord.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
}
