package kb.health.repository.record;

import jakarta.persistence.EntityManager;
import kb.health.domain.record.DietRecord;
import kb.health.domain.record.ExerciseRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecordRepositoryCustomImpl implements RecordRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<DietRecord> searchDietRecordsDynamic(Long memberId, String menuKeyword, LocalDate startDate, LocalDate endDate) {
        StringBuilder jpql = new StringBuilder("SELECT d FROM DietRecord d WHERE d.member.id = :memberId");
        boolean hasKeyword = (menuKeyword != null && !menuKeyword.isBlank());
        boolean hasStart = startDate != null;
        boolean hasEnd = endDate != null;

        if (hasKeyword) {
            jpql.append(" AND LOWER(d.diet.menu) LIKE LOWER(CONCAT('%', :menuKeyword, '%'))");
        }
        if (hasStart) {
            jpql.append(" AND d.lastModifyDate >= :start");
        }
        if (hasEnd) {
            jpql.append(" AND d.lastModifyDate < :end");
        }

        var query = em.createQuery(jpql.toString(), DietRecord.class)
                .setParameter("memberId", memberId);

        if (hasKeyword) {
            query.setParameter("menuKeyword", menuKeyword);
        }
        if (hasStart) {
            query.setParameter("start", startDate.atStartOfDay());
        }
        if (hasEnd) {
            query.setParameter("end", endDate.plusDays(1).atStartOfDay());
        }

        return query.getResultList();
    }

    @Override
    public List<ExerciseRecord> searchExerciseRecordsDynamic(Long memberId, String exerciseName, LocalDate startDate, LocalDate endDate) {
        StringBuilder jpql = new StringBuilder("SELECT e FROM ExerciseRecord e WHERE e.member.id = :memberId");

        boolean hasExerciseName = (exerciseName != null && !exerciseName.isBlank());
        boolean hasStart = startDate != null;
        boolean hasEnd = endDate != null;

        if (hasExerciseName) {
            jpql.append(" AND LOWER(e.exerciseName) LIKE LOWER(CONCAT('%', :exerciseName, '%'))");
        }
        if (hasStart) {
            jpql.append(" AND e.lastModifyDate >= :start");
        }
        if (hasEnd) {
            jpql.append(" AND e.lastModifyDate < :end");
        }

        var query = em.createQuery(jpql.toString(), ExerciseRecord.class)
                .setParameter("memberId", memberId);

        if (hasExerciseName) {
            query.setParameter("exerciseName", exerciseName);
        }
        if (hasStart) {
            query.setParameter("start", startDate.atStartOfDay());
        }
        if (hasEnd) {
            query.setParameter("end", endDate.plusDays(1).atStartOfDay());
        }

        return query.getResultList();
    }
}
