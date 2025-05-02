package kb.health.domain;

import jakarta.persistence.*;
import kb.health.domain.record.DietRecord;
import kb.health.domain.record.ExerciseRecord;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter(AccessLevel.PROTECTED)
public class DailyScore extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "daily_score_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate date; // 해당 날짜

    private double dietScore;
    private boolean exercised;
    private double totalScore;

    /**
     * 계산 로직
     */
    public static DailyScore create(Member member, List<DietRecord> dietRecordList, List<ExerciseRecord> exerciseRecordList) {
        DailyScore dailyScore = new DailyScore();

        // 영양소 목표 정보 가져오기
        DailyNutritionStandard nutritionStandard = member.getDailyNutritionStandard();

        // 1. 섭취한 총 영양소 값 계산
        int totalCalories = dietRecordList.stream()
                .mapToInt(dr -> dr.getDiet() != null ? dr.getDiet().getCalories() : 0)
                .sum();

        double totalProtein = dietRecordList.stream()
                .mapToDouble(dr -> dr.getDiet() != null ? dr.getDiet().getProtein() : 0)
                .sum();

        double totalFat = dietRecordList.stream()
                .mapToDouble(dr -> dr.getDiet() != null ? dr.getDiet().getFat() : 0)
                .sum();

        double totalCarbs = dietRecordList.stream()
                .mapToDouble(dr -> dr.getDiet() != null ? dr.getDiet().getCarbohydrates() : 0)
                .sum();

        // 2. 각 영양소 점수 계산 (목표치와 실제 섭취량 비교)
        int caloriesScore = calculateNutrientScore(totalCalories, nutritionStandard.getCalories());
        int proteinScore = calculateNutrientScore(totalProtein, nutritionStandard.getProtein());
        int fatScore = calculateNutrientScore(totalFat, nutritionStandard.getFat());
        int carbsScore = calculateNutrientScore(totalCarbs, nutritionStandard.getCarbohydrates());

        // 3. 섭취한 설탕, 섬유소, 나트륨 등에 대해서도 점수를 계산할 수 있음
        int sugarsScore = calculateNutrientScore(dietRecordList.stream()
                .mapToDouble(dr -> dr.getDiet() != null ? dr.getDiet().getSugars() : 0)
                .sum(), nutritionStandard.getSugars());

        int fiberScore = calculateNutrientScore(dietRecordList.stream()
                .mapToDouble(dr -> dr.getDiet() != null ? dr.getDiet().getFiber() : 0)
                .sum(), nutritionStandard.getFiber());

        int sodiumScore = calculateNutrientScore(dietRecordList.stream()
                .mapToDouble(dr -> dr.getDiet() != null ? dr.getDiet().getSodium() : 0)
                .sum(), nutritionStandard.getSodium());

        // 4. 전체 점수 계산 (영양소별 점수 합산)
        double dietScore =
                caloriesScore * 0.2 +
                        proteinScore * 0.2 +
                        fatScore * 0.15 +
                        carbsScore * 0.15 +
                        sugarsScore * 0.1 +
                        fiberScore * 0.1 +
                        sodiumScore * 0.1;

        // 운동 여부 체크
        boolean exercised = exerciseRecordList.stream()
                .allMatch(ExerciseRecord::isExercised);

        exercised = exercised && !exerciseRecordList.isEmpty();

        // 5. 100점 만점으로 비율 계산
        double totalScore = Math.min(dietScore, 100); // 100점을 넘지 않도록 조정

        //6. 운동 여부에 따른 가중치 적용
        if (!exercised) {
            totalScore = (totalScore * 0.7);
        }

        // dietScore 계산 후, 소수점 둘째 자리까지 버림 처리
        dietScore = Math.floor(dietScore * 100) / 100;

        // totalScore 계산 후, 소수점 둘째 자리까지 버림 처리
        totalScore = Math.floor(totalScore * 100) / 100;

        // DailyScore 객체에 값 설정
        dailyScore.assignMember(member);
        dailyScore.setDate(LocalDate.now().minusDays(1)); // 전날 날짜 설정
        dailyScore.setDietScore(dietScore); // 영양소 점수
        dailyScore.setExercised(exercised); // 운동 여부
        dailyScore.setTotalScore(totalScore); // 최종 점수

        return dailyScore;
    }

    // 영양소 점수 계산
    private static int calculateNutrientScore(double actualValue, double targetValue) {
        if (targetValue == 0) return 0; // 목표 값이 0일 경우 점수는 0
        double score = (actualValue / targetValue) * 100;
        return Math.min((int) score, 100); // 최대 100점으로 제한
    }

    /**
     * 연관관계 편의 메서드
     */
    public void assignMember(Member member) {
        this.member = member;
        if (!member.getDailyScores().contains(this)) {
            member.getDailyScores().add(this);
        }
    }

    public void deleteFromMember() {
        if (this.member != null) {
            this.member.getDailyScores().remove(this);
            this.member = null;
        }
    }


    /**
     * 테스트코드
     */

    public static DailyScore create(Member member, List<DietRecord> dietRecordList, List<ExerciseRecord> exerciseRecordList, LocalDate date) {
        DailyScore dailyScore = new DailyScore();

        // 영양소 목표 정보 가져오기
        DailyNutritionStandard nutritionStandard = member.getDailyNutritionStandard();

        // 1. 섭취한 총 영양소 값 계산
        int totalCalories = dietRecordList.stream()
                .mapToInt(dr -> dr.getDiet() != null ? dr.getDiet().getCalories() : 0)
                .sum();

        double totalProtein = dietRecordList.stream()
                .mapToDouble(dr -> dr.getDiet() != null ? dr.getDiet().getProtein() : 0)
                .sum();

        double totalFat = dietRecordList.stream()
                .mapToDouble(dr -> dr.getDiet() != null ? dr.getDiet().getFat() : 0)
                .sum();

        double totalCarbs = dietRecordList.stream()
                .mapToDouble(dr -> dr.getDiet() != null ? dr.getDiet().getCarbohydrates() : 0)
                .sum();

        // 2. 각 영양소 점수 계산 (목표치와 실제 섭취량 비교)
        int caloriesScore = calculateNutrientScore(totalCalories, nutritionStandard.getCalories());
        int proteinScore = calculateNutrientScore(totalProtein, nutritionStandard.getProtein());
        int fatScore = calculateNutrientScore(totalFat, nutritionStandard.getFat());
        int carbsScore = calculateNutrientScore(totalCarbs, nutritionStandard.getCarbohydrates());

        // 3. 섭취한 설탕, 섬유소, 나트륨 등에 대해서도 점수를 계산할 수 있음
        int sugarsScore = calculateNutrientScore(dietRecordList.stream()
                .mapToDouble(dr -> dr.getDiet() != null ? dr.getDiet().getSugars() : 0)
                .sum(), nutritionStandard.getSugars());

        int fiberScore = calculateNutrientScore(dietRecordList.stream()
                .mapToDouble(dr -> dr.getDiet() != null ? dr.getDiet().getFiber() : 0)
                .sum(), nutritionStandard.getFiber());

        int sodiumScore = calculateNutrientScore(dietRecordList.stream()
                .mapToDouble(dr -> dr.getDiet() != null ? dr.getDiet().getSodium() : 0)
                .sum(), nutritionStandard.getSodium());

        // 4. 전체 점수 계산 (영양소별 점수 합산)
        double dietScore =
                caloriesScore * 0.2 +
                        proteinScore * 0.2 +
                        fatScore * 0.15 +
                        carbsScore * 0.15 +
                        sugarsScore * 0.1 +
                        fiberScore * 0.1 +
                        sodiumScore * 0.1;

        // 운동 여부 체크
        boolean exercised = exerciseRecordList.stream()
                .allMatch(ExerciseRecord::isExercised);

        exercised = exercised && !exerciseRecordList.isEmpty();

        // 5. 100점 만점으로 비율 계산
        double totalScore = Math.min(dietScore, 100); // 100점을 넘지 않도록 조정

        //6. 운동 여부에 따른 가중치 적용
        if (!exercised) {
            totalScore = (totalScore * 0.7);
        }

        // dietScore 계산 후, 소수점 둘째 자리까지 버림 처리
        dietScore = Math.floor(dietScore * 100) / 100;

        // totalScore 계산 후, 소수점 둘째 자리까지 버림 처리
        totalScore = Math.floor(totalScore * 100) / 100;

        // DailyScore 객체에 값 설정
        dailyScore.assignMember(member);
        dailyScore.setDate(date.minusDays(1)); // 전날 날짜 설정
        dailyScore.setDietScore(dietScore); // 영양소 점수
        dailyScore.setExercised(exercised); // 운동 여부
        dailyScore.setTotalScore(totalScore); // 최종 점수

        return dailyScore;
    }

}

