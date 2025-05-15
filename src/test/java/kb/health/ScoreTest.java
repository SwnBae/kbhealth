package kb.health;

import kb.health.controller.request.MemberRegistRequest;
import kb.health.repository.DietRepository;
import kb.health.repository.RecordRepository;
import kb.health.service.MemberService;
import kb.health.service.RecordService;
import kb.health.service.ScoreService;
import kb.health.controller.ProfileController;
import kb.health.controller.request.DietRecordRequest;
import kb.health.controller.request.ExerciseRecordRequest;
import kb.health.controller.response.DailyScoreResponse;
import kb.health.controller.response.MemberProfileResponse;
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

        MemberRegistRequest memberRegistRequest1 = new MemberRegistRequest();
        memberRegistRequest1.setAccount("account1");
        memberRegistRequest1.setPassword("password1");
        memberRegistRequest1.setUserName("user1");
        memberRegistRequest1.setPhoneNumber("010-1111-1111");
        memberRegistRequest1.setHeight(170.0);
        memberRegistRequest1.setWeight(60.0);
        memberRegistRequest1.setGender(kb.health.domain.Gender.MALE);
        memberRegistRequest1.setAge(25);

        Member member2 = Member.create(memberRegistRequest1);

        MemberRegistRequest memberRegistRequest2 = new MemberRegistRequest();
        memberRegistRequest2.setAccount("account2");
        memberRegistRequest2.setPassword("password2");
        memberRegistRequest2.setUserName("user2");
        memberRegistRequest2.setPhoneNumber("010-2222-2222");
        memberRegistRequest2.setHeight(160.0);
        memberRegistRequest2.setWeight(55.0);
        memberRegistRequest2.setGender(kb.health.domain.Gender.FEMALE);
        memberRegistRequest2.setAge(24);

        Member member3 = Member.create(memberRegistRequest2);

        MemberRegistRequest memberRegistRequest3 = new MemberRegistRequest();
        memberRegistRequest3.setAccount("account3");
        memberRegistRequest3.setPassword("password3");
        memberRegistRequest3.setUserName("user3");
        memberRegistRequest3.setPhoneNumber("010-3333-3333");
        memberRegistRequest3.setHeight(175.0);
        memberRegistRequest3.setWeight(70.0);
        memberRegistRequest3.setGender(kb.health.domain.Gender.MALE);
        memberRegistRequest3.setAge(30);

        Member member4 = Member.create(memberRegistRequest3);

        MemberRegistRequest memberRegistRequest4 = new MemberRegistRequest();
        memberRegistRequest4.setAccount("account4");
        memberRegistRequest4.setPassword("password4");
        memberRegistRequest4.setUserName("user4");
        memberRegistRequest4.setPhoneNumber("010-4444-4444");
        memberRegistRequest4.setHeight(165.0);
        memberRegistRequest4.setWeight(65.0);
        memberRegistRequest4.setGender(kb.health.domain.Gender.FEMALE);
        memberRegistRequest4.setAge(28);

        Member member5 = Member.create(memberRegistRequest4);

        Long memId2 = memberService.save(member2);
        Long memId3 = memberService.save(member3);
        Long memId4 = memberService.save(member4);
        Long memId5 = memberService.save(member5);

        memberService.follow(savedMemberId, memId2);
        memberService.follow(savedMemberId, memId3);
        memberService.follow(savedMemberId, memId4);
        memberService.follow(memId5, savedMemberId);

        recordService.saveDietRecord(new DietRecordRequest(1L, 600, null, MealType.BREAKFAST), savedMemberId, LocalDate.of(2025, 4, 30));
        recordService.saveDietRecord(new DietRecordRequest(2L, 600, null, MealType.LUNCH), savedMemberId, LocalDate.of(2025, 4, 30));
        recordService.saveDietRecord(new DietRecordRequest(3L, 600, null, MealType.DINNER), savedMemberId, LocalDate.of(2025, 4, 30));

        scoreService.updateDailyScoresForAllMembers(LocalDate.of(2025, 5, 1));

        recordService.saveDietRecord(new DietRecordRequest(4L, 600, null, MealType.BREAKFAST), savedMemberId, LocalDate.of(2025, 5, 1));
        recordService.saveDietRecord(new DietRecordRequest(5L, 600, null, MealType.LUNCH), savedMemberId, LocalDate.of(2025, 5, 1));
        recordService.saveDietRecord(new DietRecordRequest(6L, 600, null, MealType.DINNER), savedMemberId, LocalDate.of(2025, 5, 1));

        // 운동 기록 추가

        Long e1 = recordService.saveExerciseRecord(new ExerciseRecordRequest("근력1",30, 300, ExerciseType.CARDIO, null), savedMemberId, LocalDate.of(2025, 5, 1));
        Long e2 = recordService.saveExerciseRecord(new ExerciseRecordRequest("근력2",45, 400, ExerciseType.WEIGHT, null), savedMemberId, LocalDate.of(2025, 5, 1));

        ExerciseRecord record1 = recordService.getExerciseRecord(e1);
        ExerciseRecord record2 = recordService.getExerciseRecord(e2);

        record1.setExercised(true);
        record2.setExercised(true);

        scoreService.updateDailyScoresForAllMembers(LocalDate.of(2025, 5, 2));

        /**
         * 오늘 영양소 체크
         */

        recordService.saveDietRecord(new DietRecordRequest(7L, 600,  null, MealType.BREAKFAST), savedMemberId, LocalDate.of(2025, 5, 15));
        recordService.saveDietRecord(new DietRecordRequest(8L, 600,  null, MealType.LUNCH), savedMemberId, LocalDate.of(2025, 5, 15));
    }

    private Member createMember() {
        String account = "account";
        String userName = "member1";
        String password = "password";
        String phoneNumber = "010-0000-0000";

        Member member = Member.create(account, userName, password, phoneNumber);

        return member;
    }

    @Test
    @Rollback(false)
    void testDailyScore() {
        Member member = memberService.findById(savedMemberId);

//        ResponseEntity<MemberProfileResponse> response = profileController.getProfile(member.getAccount());
//        MemberProfileResponse profile = response.getBody();
//
//        System.out.println("== [프로필 정보] ==");
//        System.out.printf("회원 ID: %d%n", profile.getMemberId());
//        System.out.printf("닉네임: %s%n", profile.getUserName());
//        System.out.printf("총점: %.2f%n", profile.getTotalScore());
//        System.out.printf("기본점수: %.2f%n", profile.getBaseScore());
//        System.out.printf("프로필 이미지 URL: %s%n", profile.getProfileImageUrl());
//
//        System.out.println("\n== [팔로잉/팔로워 수] ==");
//        System.out.printf("팔로잉 수: %d%n", profile.getFollowingCount());
//        System.out.printf("팔로워 수: %d%n", profile.getFollowerCount());
//
//        System.out.println("\n== [오늘의 영양소 목표 달성률] ==");
//        NutritionAchievementResponse nutrition = profile.getTodayAchievement();
//        System.out.printf("열량: %.2f%%%n", nutrition.getCaloriesRate() * 100);
//        System.out.printf("단백질: %.2f%%%n", nutrition.getProteinRate() * 100);
//        System.out.printf("지방: %.2f%%%n", nutrition.getFatRate() * 100);
//        System.out.printf("탄수화물: %.2f%%%n", nutrition.getCarbRate() * 100);
//        System.out.printf("당류: %.2f%%%n", nutrition.getSugarsRate() * 100);
//        System.out.printf("식이섬유: %.2f%%%n", nutrition.getFiberRate() * 100);
//        System.out.printf("나트륨: %.2f%%%n", nutrition.getSodiumRate() * 100);
//
//        System.out.println("\n== [최근 10일 점수] ==");
//        for (DailyScoreResponse dailyScore : profile.getLast10DaysScores()) {
//            System.out.printf("날짜: %s | 총점: %.2f%n", dailyScore.getDate(), dailyScore.getTotalScore());
//        }
    }
}

