package kb.health;

import kb.health.repository.DietRepository;
import kb.health.repository.RecordRepository;
import kb.health.service.MemberService;
import kb.health.service.RecordService;
import kb.health.service.ScoreService;
import kb.health.controller.ProfileController;
import kb.health.controller.request.DietRecordRequest;
import kb.health.controller.request.ExerciseRecordRequest;
import kb.health.controller.response.DailyScoreResponse;
import kb.health.controller.response.MemberResponse;
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
import org.springframework.http.ResponseEntity;
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

    @Autowired
    ProfileController profileController;

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

        Long e1 = recordService.saveExerciseRecord(new ExerciseRecordRequest("근력1",30, 300, ExerciseType.CARDIO), savedMemberId, LocalDate.of(2025, 5, 1));
        Long e2 = recordService.saveExerciseRecord(new ExerciseRecordRequest("근력2",45, 400, ExerciseType.WEIGHT), savedMemberId, LocalDate.of(2025, 5, 1));

        ExerciseRecord record1 = recordService.getExerciseRecord(e1);
        ExerciseRecord record2 = recordService.getExerciseRecord(e2);

        record1.setExercised(true);
        record2.setExercised(true);

        scoreService.updateDailyScoresForAllMembers(LocalDate.of(2025, 5, 2));

        /**
         * 오늘 영양소 체크
         */
        Long d7 = addDiet("국밥", 900);
        Long d8 = addDiet("타코", 700);

        recordService.saveDietRecord(new DietRecordRequest(d7, MealType.BREAKFAST), savedMemberId, LocalDate.of(2025, 5, 7));
        recordService.saveDietRecord(new DietRecordRequest(d8, MealType.LUNCH), savedMemberId, LocalDate.of(2025, 5, 7));
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
    @Rollback(value = false)
    void testDailyScore() {
        Member member = memberService.findById(savedMemberId);

        ResponseEntity<MemberResponse> response = profileController.getProfile(member.getAccount());
        MemberResponse profile = response.getBody();

        System.out.println("== [프로필 정보] ==");
        System.out.printf("회원 ID: %d%n", profile.getMemberId());
        System.out.printf("닉네임: %s%n", profile.getUserName());
        System.out.printf("총점: %.2f%n", profile.getTotalScore());
        System.out.printf("기본점수: %.2f%n", profile.getBaseScore());
        System.out.printf("프로필 이미지 URL: %s%n", profile.getProfileImageUrl());

        System.out.println("\n== [오늘의 영양소 목표 달성률] ==");
        NutritionAchievementResponse nutrition = profile.getTodayAchievement();
        System.out.printf("열량: %.2f%%%n", nutrition.getCaloriesRate() * 100);
        System.out.printf("단백질: %.2f%%%n", nutrition.getProteinRate() * 100);
        System.out.printf("지방: %.2f%%%n", nutrition.getFatRate() * 100);
        System.out.printf("탄수화물: %.2f%%%n", nutrition.getCarbRate() * 100);
        System.out.printf("당류: %.2f%%%n", nutrition.getSugarsRate() * 100);
        System.out.printf("식이섬유: %.2f%%%n", nutrition.getFiberRate() * 100);
        System.out.printf("나트륨: %.2f%%%n", nutrition.getSodiumRate() * 100);

        System.out.println("\n== [최근 10일 점수] ==");
        for (DailyScoreResponse dailyScore : profile.getLast10DaysScores()) {
            System.out.printf("날짜: %s | 총점: %.2f%n", dailyScore.getDate(), dailyScore.getTotalScore());
        }
    }
}

