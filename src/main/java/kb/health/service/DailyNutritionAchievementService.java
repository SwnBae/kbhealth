package kb.health.service;

import kb.health.controller.response.DailyNutritionAchievementResponse;
import kb.health.domain.DailyNutritionAchievement;
import kb.health.domain.DailyNutritionStandard;
import kb.health.domain.Member;
import kb.health.domain.record.DietRecord;
import kb.health.repository.DailyNutritionAchievementRepository;
import kb.health.repository.MemberRepository;
import kb.health.repository.record.DietRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DailyNutritionAchievementService {

    private final DailyNutritionAchievementRepository dailyNutritionAchievementRepository;
    private final MemberRepository memberRepository;
    private final DietRecordRepository dietRecordRepository;

    /**
     * 매일 0시 10분에 모든 멤버의 전날 영양 달성률을 계산하여 DB에 저장
     */
    @Transactional
    @Scheduled(cron = "0 10 0 * * *")
    public void updateDailyNutritionAchievementsForAllMembers() {
        // 전날 데이터를 처리 (0시에 실행되므로 전날 데이터가 완성됨)
        LocalDate yesterday = LocalDate.now().minusDays(1);

        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("=============================");
            System.out.println("영양 달성률 계산: " + member.getUserName());

            // 해당 날짜의 식단 기록 조회
            List<DietRecord> dietRecords = findDietRecordsByMemberAndDate(member, yesterday);

            System.out.println("식단 기록 수: " + dietRecords.size());

            // 이미 해당 날짜의 영양 달성률이 저장되어 있는지 확인
            Optional<DailyNutritionAchievement> existingAchievement =
                    dailyNutritionAchievementRepository.findByMemberAndDate(member, yesterday);

            if (existingAchievement.isPresent()) {
                System.out.println("이미 저장된 영양 달성률이 있음 - 업데이트");
                // 기존 데이터 업데이트
                updateExistingAchievement(existingAchievement.get(), dietRecords, member.getDailyNutritionStandard());
            } else {
                System.out.println("새로운 영양 달성률 생성");
                // 새로운 영양 달성률 생성 및 저장
                DailyNutritionAchievement newAchievement = DailyNutritionAchievement.create(
                        member, dietRecords, member.getDailyNutritionStandard(), yesterday);
                dailyNutritionAchievementRepository.save(newAchievement);
            }

            System.out.println("영양 달성률 저장 완료");
        }
    }

    /**
     * 테스트용 - 특정 날짜의 영양 달성률 계산
     */
    @Transactional
    public void updateDailyNutritionAchievementsForAllMembers(LocalDate date) {
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            System.out.println("=============================");
            System.out.println("영양 달성률 계산: " + member.getUserName() + " - " + date);

            List<DietRecord> dietRecords = findDietRecordsByMemberAndDate(member, date);

            System.out.println("식단 기록 수: " + dietRecords.size());

            if (dietRecords.isEmpty()) {
                System.out.println("식단 기록이 없음 - 건너뜀");
                continue;
            }

            // 기존 데이터 확인
            Optional<DailyNutritionAchievement> existingAchievement =
                    dailyNutritionAchievementRepository.findByMemberAndDate(member, date);

            if (existingAchievement.isPresent()) {
                updateExistingAchievement(existingAchievement.get(), dietRecords, member.getDailyNutritionStandard());
            } else {
                DailyNutritionAchievement newAchievement = DailyNutritionAchievement.create(
                        member, dietRecords, member.getDailyNutritionStandard(), date);
                dailyNutritionAchievementRepository.save(newAchievement);
            }

            System.out.println("영양 달성률 저장 완료");
        }
    }

    /**
     * 기존 영양 달성률 데이터 업데이트
     */
    private void updateExistingAchievement(DailyNutritionAchievement achievement,
                                           List<DietRecord> dietRecords,
                                           DailyNutritionStandard standard) {
        // 새로운 값으로 재계산
        DailyNutritionAchievement newData = DailyNutritionAchievement.create(
                achievement.getMember(), dietRecords, standard, achievement.getDate());

        // 기존 엔티티 업데이트
        achievement.setCaloriesRate(newData.getCaloriesRate());
        achievement.setProteinRate(newData.getProteinRate());
        achievement.setFatRate(newData.getFatRate());
        achievement.setCarbRate(newData.getCarbRate());
        achievement.setSugarsRate(newData.getSugarsRate());
        achievement.setFiberRate(newData.getFiberRate());
        achievement.setSodiumRate(newData.getSodiumRate());
    }

    /**
     * 특정 회원의 특정 날짜 영양 달성률 조회
     */
    public DailyNutritionAchievement getNutritionAchievement(Member member, LocalDate date) {
        return dailyNutritionAchievementRepository.findByMemberAndDate(member, date)
                .orElse(null);
    }

    /**
     * 특정 회원의 최근 10일 영양 달성률 조회
     */
    public List<DailyNutritionAchievement> getLast10DaysNutritionAchievements(Member member) {
        return dailyNutritionAchievementRepository.findTop10ByMemberOrderByDateDesc(member.getId());
    }

    public List<DailyNutritionAchievementResponse> getLast10DaysNutritionResponses(Member member) {
        List<DailyNutritionAchievement> achievements = dailyNutritionAchievementRepository
                .findTop10ByMemberOrderByDateDesc(member.getId());

        return achievements.stream()
                .map(DailyNutritionAchievementResponse::create)
                .sorted((a, b) -> a.getDate().compareTo(b.getDate())) // 날짜 오름차순 정렬
                .toList();
    }

    /**
     * 특정 회원의 날짜 범위별 영양 달성률 조회
     */
    public List<DailyNutritionAchievement> getNutritionAchievementsByDateRange(Member member,
                                                                               LocalDate startDate,
                                                                               LocalDate endDate) {
        return dailyNutritionAchievementRepository.findByMemberAndDateBetween(member, startDate, endDate);
    }

    /**
     * 특정 회원의 특정 날짜 영양 달성률 강제 갱신
     */
    @Transactional
    public void refreshNutritionAchievement(Member member, LocalDate date) {
        List<DietRecord> dietRecords = findDietRecordsByMemberAndDate(member, date);

        Optional<DailyNutritionAchievement> existingAchievement =
                dailyNutritionAchievementRepository.findByMemberAndDate(member, date);

        if (existingAchievement.isPresent()) {
            updateExistingAchievement(existingAchievement.get(), dietRecords, member.getDailyNutritionStandard());
        } else {
            DailyNutritionAchievement newAchievement = DailyNutritionAchievement.create(
                    member, dietRecords, member.getDailyNutritionStandard(), date);
            dailyNutritionAchievementRepository.save(newAchievement);
        }
    }

    /**
     * 날짜 범위 조회 헬퍼 메서드
     */
    private List<DietRecord> findDietRecordsByMemberAndDate(Member member, LocalDate date) {
        LocalDateTime start = date.atStartOfDay().minusDays(1);
        LocalDateTime end = date.atStartOfDay();
        return dietRecordRepository.findByMemberAndDateRange(member, start, end);
    }
}