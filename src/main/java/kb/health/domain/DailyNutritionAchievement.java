package kb.health.domain;

import jakarta.persistence.*;
import kb.health.domain.record.DietRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyNutritionAchievement extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "daily_nutrition_achievement_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate date;

    private double caloriesRate;
    private double proteinRate;
    private double fatRate;
    private double carbRate;
    private double sugarsRate;
    private double fiberRate;
    private double sodiumRate;

    // 생성 메서드
    public static DailyNutritionAchievement create(Member member, List<DietRecord> records, DailyNutritionStandard standard, LocalDate date) {
        double totalCalories = 0;
        double totalProtein = 0;
        double totalFat = 0;
        double totalCarbs = 0;
        double totalSugars = 0;
        double totalFiber = 0;
        double totalSodium = 0;

        for (DietRecord record : records) {
            if (record.getDiet() == null) continue;

            double factor = record.getAmount() / 100.0; // 100g 기준으로 비례 계산

            totalCalories += record.getDiet().getCalories() * factor;
            totalProtein += record.getDiet().getProtein() * factor;
            totalFat += record.getDiet().getFat() * factor;
            totalCarbs += record.getDiet().getCarbohydrates() * factor;
            totalSugars += record.getDiet().getSugars() * factor;
            totalFiber += record.getDiet().getFiber() * factor;
            totalSodium += record.getDiet().getSodium() * factor;
        }

        DailyNutritionAchievement achievement = new DailyNutritionAchievement();
        achievement.setMember(member);
        achievement.setDate(date);

        // 달성률 계산
        achievement.setCaloriesRate(rate(totalCalories, standard.getCalories()));
        achievement.setProteinRate(rate(totalProtein, standard.getProtein()));
        achievement.setFatRate(rate(totalFat, standard.getFat()));
        achievement.setCarbRate(rate(totalCarbs, standard.getCarbohydrates()));
        achievement.setSugarsRate(rate(totalSugars, standard.getSugars()));
        achievement.setFiberRate(rate(totalFiber, standard.getFiber()));
        achievement.setSodiumRate(rate(totalSodium, standard.getSodium()));

        return achievement;
    }

    private static double rate(double intake, double standard) {
        if (standard == 0) return 0;
        return Math.min(1.0, intake / standard);
    }
}