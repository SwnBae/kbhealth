package kb.health.repository;

import kb.health.domain.DailyNutritionAchievement;
import kb.health.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyNutritionAchievementRepository extends JpaRepository<DailyNutritionAchievement, Long> {

    // 특정 회원의 특정 날짜 영양 달성률 조회
    Optional<DailyNutritionAchievement> findByMemberAndDate(Member member, LocalDate date);

    // 특정 회원의 모든 영양 달성률 조회 (날짜 내림차순)
    List<DailyNutritionAchievement> findByMemberOrderByDateDesc(Member member);

    // 특정 회원의 최근 N일 영양 달성률 조회
    @Query("SELECT d FROM DailyNutritionAchievement d WHERE d.member = :member ORDER BY d.date DESC")
    List<DailyNutritionAchievement> findTopNByMemberOrderByDateDesc(@Param("member") Member member);

    // 특정 회원의 날짜 범위별 영양 달성률 조회
    @Query("SELECT d FROM DailyNutritionAchievement d WHERE d.member = :member AND d.date BETWEEN :startDate AND :endDate ORDER BY d.date")
    List<DailyNutritionAchievement> findByMemberAndDateBetween(@Param("member") Member member,
                                                               @Param("startDate") LocalDate startDate,
                                                               @Param("endDate") LocalDate endDate);

    // 특정 회원의 최근 10일 영양 달성률 조회
    @Query(value = "SELECT * FROM daily_nutrition_achievement WHERE member_id = :memberId ORDER BY date DESC LIMIT 10", nativeQuery = true)
    List<DailyNutritionAchievement> findTop10ByMemberOrderByDateDesc(@Param("memberId") Long memberId);
}