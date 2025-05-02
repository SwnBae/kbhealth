package kb.health.record;

import kb.health.Repository.DietRepository;
import kb.health.Service.*;
import kb.health.domain.BodyInfo;
import kb.health.domain.DailyNutritionStandard;
import kb.health.domain.Gender;
import kb.health.domain.Member;
import kb.health.domain.record.*;
import kb.health.controller.request.DietRecordRequest;
import kb.health.controller.request.DietRequest;
import kb.health.controller.response.DietRecordResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class DietTest {

    @Autowired RecordService recordService;
    @Autowired MemberService memberService;
    @Autowired DietRepository dietRepository;

    Long savedMemberId;

    @BeforeEach
    void setUp() {
        Member member = Member.create("account", "Test", "password", "010-0000-0000");

        // BodyInfo와 DailyNutritionStandard 설정
        BodyInfo bodyInfo = new BodyInfo(175.0, 70.0, Gender.MALE, 30);
        member.setBodyInfo(bodyInfo);

        DailyNutritionStandard standard = DailyNutritionStandard.calculate(bodyInfo);
        member.setDailyNutritionStandard(standard);

        savedMemberId = memberService.save(member);
    }

    /**
     * 음식 추가, 삭제
     */
    @Test
    void 음식_추가() {
        // given
        DietRequest request = new DietRequest("연어 스테이크", 450);

        // when
        Long savedId = recordService.addDiet(request);
        Diet savedDiet = dietRepository.findById(savedId);

        // then
        assertThat(savedDiet).isNotNull();
        assertThat(savedDiet.getMenu()).isEqualTo("연어 스테이크");
        assertThat(savedDiet.getCalories()).isEqualTo(450);
    }

    @Test
    void 음식_삭제() {
        // given
        DietRequest request = new DietRequest("햄버거", 700);
        Long savedId = recordService.addDiet(request);

        // when
        recordService.deleteDiet(savedId);

        // then
        Diet deletedDiet = dietRepository.findById(savedId);
        assertThat(deletedDiet).isNull();
    }

    /**
     * 식단 기록 삽입, 삭제, 수정
     */
    @Test
    void 식단_기록_삽입() {
        //given
        DietRequest dietRequest = new DietRequest("닭가슴살+샐러드", 350);

        Long dietId = recordService.addDiet(dietRequest);
        Diet diet = dietRepository.findById(dietId);

        DietRecordRequest recordRequest = new DietRecordRequest(dietId,MealType.LUNCH);

        //when
        recordService.saveDietRecord(recordRequest, savedMemberId);

        //then
        List<DietRecordResponse> result = recordService.getDietRecords(savedMemberId);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDietId()).isEqualTo(dietId);
    }

    @Test
    void 식단_기록_조회() {
        // given
        Long dietId1 = recordService.addDiet(new DietRequest("샐러드", 150));
        Long dietId2 = recordService.addDiet(new DietRequest("스테이크", 600));

        recordService.saveDietRecord(new DietRecordRequest(dietId1, MealType.BREAKFAST), savedMemberId);
        recordService.saveDietRecord(new DietRecordRequest(dietId2, MealType.DINNER), savedMemberId);

        // when
        List<DietRecordResponse> records = recordService.getDietRecords(savedMemberId);

        // then
        assertThat(records).hasSize(2);
        assertThat(records)
                .extracting(record -> record.getDietMenu())
                .containsExactlyInAnyOrder("샐러드", "스테이크");
    }

    @Test
    void 식단_기록_수정() {
        // given
        Long dietId1 = recordService.addDiet(new DietRequest("계란후라이", 200));
        Long dietId2 = recordService.addDiet(new DietRequest("닭가슴살", 300));

        // 처음에는 dietId1 (계란후라이)로 기록
        recordService.saveDietRecord(new DietRecordRequest(dietId1, MealType.BREAKFAST), savedMemberId);
        Long recordId = recordService.getDietRecords(savedMemberId).get(0).getId();

        // when: 메뉴를 dietId2(닭가슴살)로, 식사시간을 DINNER로 수정
        DietRecordRequest updateRequest = new DietRecordRequest(dietId2, MealType.DINNER);
        recordService.updateDietRecord(savedMemberId, recordId, updateRequest);

        // then
        DietRecord updatedRecord = recordService.getDietRecord(recordId);
        assertThat(updatedRecord.getDiet().getMenu()).isEqualTo("닭가슴살");
        assertThat(updatedRecord.getMealType()).isEqualTo(MealType.DINNER);
    }


    @Test
    void 식단_기록_삭제() {
        // given
        Long dietId = recordService.addDiet(new DietRequest("라면", 500));
        recordService.saveDietRecord(new DietRecordRequest(dietId, MealType.LUNCH), savedMemberId);

        List<DietRecordResponse> before = recordService.getDietRecords(savedMemberId);
        Long recordId = before.get(0).getId();

        // when
        recordService.deleteDietRecord(savedMemberId, recordId);

        // then
        List<DietRecordResponse> after = recordService.getDietRecords(savedMemberId);
        assertThat(after).isEmpty();
    }

    private Diet getFood(){
        Diet diet = new Diet();
        diet.setMenu("햄버거");
        diet.setCalories(500);

        return diet;
    }
}