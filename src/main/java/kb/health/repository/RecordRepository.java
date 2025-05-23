package kb.health.repository;

import jakarta.persistence.EntityManager;
import kb.health.domain.Member;
import kb.health.domain.record.Diet;
import kb.health.domain.record.DietRecord;
import kb.health.domain.record.ExerciseRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecordRepository {

    private final EntityManager em;

    // 식단 레코드 저장
    public Long saveDietRecord(DietRecord dietRecord) {
        em.persist(dietRecord);

        return dietRecord.getId();
    }

    // 운동 레코드 저장
    public Long saveExerciseRecord(ExerciseRecord exerciseRecord) {
        em.persist(exerciseRecord);

        return exerciseRecord.getId();
    }

    // 특정 Member의 모든 식단 기록 조회
    public List<DietRecord> findDietRecordsByMember(Long memberId) {
        return em.createQuery("SELECT d FROM DietRecord d WHERE d.member.id = :memberId", DietRecord.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    //모든 식단 기록 조회
    public List<DietRecord> findAllDietRecord() {
        return em.createQuery("SELECT d FROM DietRecord d", DietRecord.class)
                .getResultList();
    }

    // 특정 Member의 모든 운동 기록 조회
    public List<ExerciseRecord> findExerciseRecordsByMember(Long memberId) {
        return em.createQuery("SELECT e FROM ExerciseRecord e WHERE e.member.id = :memberId", ExerciseRecord.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    // 식단 레코드 단건 조회
    public DietRecord findDietRecordById(Long id) {
        return em.find(DietRecord.class, id);
    }

    // 운동 레코드 단건 조회
    public ExerciseRecord findExerciseRecordById(Long id) {
        return em.find(ExerciseRecord.class, id);
    }

    // 식단 레코드 삭제
    public void deleteDietRecord(Long id) {
        DietRecord dietRecord = em.find(DietRecord.class, id);

        if (dietRecord != null) {
            em.remove(dietRecord);
        }
    }

    // 운동 레코드 삭제
    public void deleteExerciseRecord(Long id) {
        ExerciseRecord exerciseRecord = em.find(ExerciseRecord.class, id);

        if (exerciseRecord != null) {
            em.remove(exerciseRecord);
        }
    }

    public List<DietRecord> findDietRecordsByDiet(Diet diet) {
        return em.createQuery("SELECT d FROM DietRecord d WHERE d.diet = :diet", DietRecord.class)
                .setParameter("diet", diet)
                .getResultList();
    }

    /**
     * 전날 00시 ~ 해당 00시까지
     */
    public List<DietRecord> findDietRecordsByMemberAndDate(Member member, LocalDate date) {
        LocalDateTime start = date.atStartOfDay().minusDays(1);
        LocalDateTime end = date.atStartOfDay();

//        LocalDateTime start = date.atStartOfDay();
//        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return em.createQuery(
                        "SELECT d FROM DietRecord d WHERE d.member = :member AND d.lastModifyDate >= :start AND d.lastModifyDate < :end",
                        DietRecord.class)
                .setParameter("member", member)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    public List<ExerciseRecord> findExerciseRecordsByMemberAndDate(Member member, LocalDate date) {
        LocalDateTime start = date.atStartOfDay().minusDays(1);
        LocalDateTime end = date.atStartOfDay();
//        LocalDateTime start = date.atStartOfDay();
//        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return em.createQuery(
                        "SELECT e FROM ExerciseRecord e WHERE e.member = :member AND e.lastModifyDate >= :start AND e.lastModifyDate < :end",
                        ExerciseRecord.class)
                .setParameter("member", member)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    /**
     * 당일만 조회하는 메서드
     */
    public List<DietRecord> findDietRecordsByMemberAndDateOnly(Member member, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return em.createQuery(
                        "SELECT d FROM DietRecord d WHERE d.member = :member AND d.lastModifyDate >= :start AND d.lastModifyDate < :end",
                        DietRecord.class)
                .setParameter("member", member)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    public List<ExerciseRecord> findExerciseRecordsByMemberAndDateOnly(Member member, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return em.createQuery(
                        "SELECT e FROM ExerciseRecord e WHERE e.member = :member AND e.lastModifyDate >= :start AND e.lastModifyDate < :end",
                        ExerciseRecord.class)
                .setParameter("member", member)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    /**
     * 식단 검색
     */
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

    public List<ExerciseRecord> searchExerciseRecordsDynamic(Long memberId, String exerciseName, LocalDate startDate, LocalDate endDate) {
        StringBuilder jpql = new StringBuilder("SELECT e FROM ExerciseRecord e WHERE e.member.id = :memberId");

        // 검색 조건이 있는지 확인
        boolean hasExerciseName = (exerciseName != null && !exerciseName.isBlank());
        boolean hasStart = startDate != null;
        boolean hasEnd = endDate != null;

        // exerciseName 조건 추가
        if (hasExerciseName) {
            jpql.append(" AND LOWER(e.exerciseName) LIKE LOWER(CONCAT('%', :exerciseName, '%'))");
        }

        // 날짜 조건 추가
        if (hasStart) {
            jpql.append(" AND e.lastModifyDate >= :start");
        }
        if (hasEnd) {
            jpql.append(" AND e.lastModifyDate < :end");
        }

        // JPQL 쿼리 생성
        var query = em.createQuery(jpql.toString(), ExerciseRecord.class)
                .setParameter("memberId", memberId);

        // 조건에 따라 파라미터 설정
        if (hasExerciseName) {
            query.setParameter("exerciseName", exerciseName);
        }
        if (hasStart) {
            query.setParameter("start", startDate.atStartOfDay());
        }
        if (hasEnd) {
            query.setParameter("end", endDate.plusDays(1).atStartOfDay());
        }

        // 결과 반환
        return query.getResultList();
    }


}
