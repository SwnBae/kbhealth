package kb.health;

import kb.health.Repository.DietRepository;
import kb.health.Repository.RecordRepository;
import kb.health.Service.MemberService;
import kb.health.Service.RecordService;
import kb.health.Service.ScoreService;
import kb.health.controller.request.DietRecordRequest;
import kb.health.controller.request.DietRequest;
import kb.health.controller.request.ExerciseRecordRequest;
import kb.health.controller.response.NutritionAchievementResponse;
import kb.health.domain.*;
import kb.health.domain.record.Diet;
import kb.health.domain.record.ExerciseRecord;
import kb.health.domain.record.ExerciseType;
import kb.health.domain.record.MealType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@Transactional
class ScoreTest {

    @Autowired
    RecordService recordService;
    @Autowired
    MemberService memberService;
    @Autowired
    ScoreService scoreService;  // 일일점수 계산을 위한 서비스
    @Autowired
    DietRepository dietRepository;
    @Autowired
    RecordRepository recordRepository;

    Long savedMemberId;

    @BeforeEach
    void setUp() {
        // 회원 정보 설정
        Member member = createMember();

        // BodyInfo와 DailyNutritionStandard 설정
        BodyInfo bodyInfo = new BodyInfo(175.0, 70.0, Gender.MALE, 30);
        member.setBodyInfo(bodyInfo);

        DailyNutritionStandard standard = DailyNutritionStandard.calculate(bodyInfo);
        member.setDailyNutritionStandard(standard);

        savedMemberId = memberService.save(member);

        // 식단 추가
        Long d1 = addDiet("연어 스테이크", 450);
        Long d2 = addDiet("햄버거", 700);
        Long d3 = addDiet("닭가슴살+샐러드", 350);

        recordService.saveDietRecord(new DietRecordRequest(d1, MealType.BREAKFAST), savedMemberId, LocalDate.of(2025, 4, 30));
        recordService.saveDietRecord(new DietRecordRequest(d2, MealType.LUNCH), savedMemberId, LocalDate.of(2025, 4, 30));
        recordService.saveDietRecord(new DietRecordRequest(d3, MealType.DINNER), savedMemberId, LocalDate.of(2025, 4, 30));

        scoreService.updateDailyScoresForAllMembers(LocalDate.of(2025, 5, 1));

        Long d4 = addDiet("피자", 200);
        Long d5 = addDiet("햄버거", 500);
        Long d6 = addDiet("돈가스", 350);

        recordService.saveDietRecord(new DietRecordRequest(d4, MealType.BREAKFAST), savedMemberId, LocalDate.of(2025, 5, 1));
        recordService.saveDietRecord(new DietRecordRequest(d5, MealType.LUNCH), savedMemberId, LocalDate.of(2025, 5, 1));
        recordService.saveDietRecord(new DietRecordRequest(d6, MealType.DINNER), savedMemberId, LocalDate.of(2025, 5, 1));

        // 운동 기록 추가

        Long e1 = recordService.saveExerciseRecord(new ExerciseRecordRequest(30, 300, ExerciseType.CARDIO), savedMemberId, LocalDate.of(2025, 5, 1));
        Long e2 = recordService.saveExerciseRecord(new ExerciseRecordRequest(45, 400, ExerciseType.WEIGHT), savedMemberId, LocalDate.of(2025, 5, 1));

        ExerciseRecord record1 = recordService.getExerciseRecord(e1);
        ExerciseRecord record2 = recordService.getExerciseRecord(e2);

        record1.setExercised(true);
        record2.setExercised(true);
    }

    private Member createMember() {
        String account = "account";
        String userName = "member1";
        String password = "password";
        String phoneNumber = "010-0000-0000";

        Member member = Member.create(account, userName, password, phoneNumber);

        return member;
    }

    private Long addDiet(String menu, int calories) {
        Diet diet = new Diet();
        diet.setMenu(menu);
        diet.setCategory("일반식");        // 카테고리 예시
        diet.setStandardAmount(100);       // 기준 섭취량 (예: 100g)
        diet.setCalories(calories);
        diet.setProtein(25.0);             // 단백질 (g)
        diet.setFat(10.0);                 // 지방 (g)
        diet.setCarbohydrates(30.0);       // 탄수화물 (g)
        diet.setSugars(5.0);               // 당류 (g)
        diet.setFiber(3.0);                // 식이섬유 (g)
        diet.setSodium(500.0);             // 나트륨 (mg)

        return dietRepository.save(diet);
    }

    @Test
    void testDailyScore() {
        // 일일 점수 계산
        scoreService.updateDailyScoresForAllMembers();
        Member member = memberService.findById(savedMemberId);

        LocalDate today = LocalDate.now();

        NutritionAchievementResponse nutritionAchievementResponse = recordService.getNutritionAchievement(savedMemberId, today.minusDays(1));
        System.out.println("Nutrition Achievement Response1: " + nutritionAchievementResponse.toString());


        nutritionAchievementResponse = recordService.getNutritionAchievement(savedMemberId, today);
        System.out.println("Nutrition Achievement Response2: " + nutritionAchievementResponse.toString());



        for (DailyScore dailyScore : member.getDailyScores()) {
            System.out.println(dailyScore.getTotalScore());
            System.out.println(dailyScore.getDietScore());
        }
    }
}

