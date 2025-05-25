package kb.health.repository.record;

import kb.health.domain.Member;
import kb.health.domain.record.Diet;
import kb.health.domain.record.DietRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DietRecordRepository extends JpaRepository<DietRecord, Long> {

    // 특정 Member의 모든 식단 기록 조회
    List<DietRecord> findByMemberId(Long memberId);

    // 특정 Diet과 연관된 식단 기록 조회
    List<DietRecord> findByDiet(Diet diet);

    // 전날 00시 ~ 해당 00시까지 조회
    @Query("SELECT d FROM DietRecord d WHERE d.member = :member AND d.lastModifyDate >= :start AND d.lastModifyDate < :end")
    List<DietRecord> findByMemberAndDateRange(@Param("member") Member member,
                                              @Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);

    // 당일만 조회하는 메서드
    @Query("SELECT d FROM DietRecord d WHERE d.member = :member AND d.lastModifyDate >= :start AND d.lastModifyDate < :end")
    List<DietRecord> findByMemberAndDateOnly(@Param("member") Member member,
                                             @Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);
}
