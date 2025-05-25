package kb.health.repository.record;

import kb.health.domain.Member;
import kb.health.domain.record.ExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {

    // 특정 Member의 모든 운동 기록 조회
    List<ExerciseRecord> findByMemberId(Long memberId);

    // 전날 00시 ~ 해당 00시까지 조회
    @Query("SELECT e FROM ExerciseRecord e WHERE e.member = :member AND e.lastModifyDate >= :start AND e.lastModifyDate < :end")
    List<ExerciseRecord> findByMemberAndDateRange(@Param("member") Member member,
                                                  @Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);

    // 당일만 조회하는 메서드
    @Query("SELECT e FROM ExerciseRecord e WHERE e.member = :member AND e.lastModifyDate >= :start AND e.lastModifyDate < :end")
    List<ExerciseRecord> findByMemberAndDateOnly(@Param("member") Member member,
                                                 @Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);
}
