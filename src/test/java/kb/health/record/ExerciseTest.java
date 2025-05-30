package kb.health.record;

import kb.health.repository.record.ExerciseRecordRepository;
import kb.health.service.*;
import kb.health.domain.BodyInfo;
import kb.health.domain.DailyNutritionStandard;
import kb.health.domain.Gender;
import kb.health.domain.Member;
import kb.health.domain.record.*;
import kb.health.controller.request.ExerciseRecordRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ExerciseTest {

    @Autowired
    RecordService recordService;
    @Autowired
    MemberService memberService;
    @Autowired
    ExerciseRecordRepository exerciseRecordRepository;

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
     * 운동 기록 삽입
     */
    @Test
    void 운동_기록_삽입() {
        // given
        ExerciseRecordRequest request = new ExerciseRecordRequest("근력1", 30, 300, ExerciseType.CARDIO, null);

        // when
        Long savedId = recordService.saveExerciseRecord(request, savedMemberId);
        ExerciseRecord savedRecord = exerciseRecordRepository.findById(savedId).orElseThrow();

        // then
        assertThat(savedRecord).isNotNull();
        assertThat(savedRecord.getExerciseName()).isEqualTo("근력1");
        assertThat(savedRecord.getDurationMinutes()).isEqualTo(30);
        assertThat(savedRecord.getCaloriesBurned()).isEqualTo(300);
        assertThat(savedRecord.getExerciseType()).isEqualTo(ExerciseType.CARDIO);
    }

    /**
     * 운동 기록 수정
     */
    @Test
    void 운동_기록_수정() {
        // given
        ExerciseRecordRequest request = new ExerciseRecordRequest("근력1",30, 300, ExerciseType.CARDIO, null);

        Long savedId = recordService.saveExerciseRecord(request, savedMemberId);
        ExerciseRecord savedRecord = exerciseRecordRepository.findById(savedId).orElseThrow();

        // when: 운동시간, 칼로리, 운동 타입 수정
        ExerciseRecordRequest updateRequest = new ExerciseRecordRequest();
        updateRequest.setExerciseName("근력2");
        updateRequest.setDurationMinutes(45);
        updateRequest.setCaloriesBurned(400);
        updateRequest.setExerciseType(ExerciseType.WEIGHT);

        recordService.updateExerciseRecord(savedMemberId, savedRecord.getId(), updateRequest);

        // then
        ExerciseRecord updatedRecord = recordService.getExerciseRecord(savedId);
        assertThat(updatedRecord.getExerciseName()).isEqualTo("근력2");
        assertThat(updatedRecord.getDurationMinutes()).isEqualTo(45);
        assertThat(updatedRecord.getCaloriesBurned()).isEqualTo(400);
        assertThat(updatedRecord.getExerciseType()).isEqualTo(ExerciseType.WEIGHT);
    }

    /**
     * 운동 기록 삭제
     */
    @Test
    void 운동_기록_삭제() {
        // given
        ExerciseRecordRequest request = new ExerciseRecordRequest("근력1",30, 300, ExerciseType.CARDIO, null);

        Long savedId = recordService.saveExerciseRecord(request, savedMemberId);

        // when
        recordService.deleteExerciseRecord(savedMemberId, savedId);

        // then
        ExerciseRecord deletedRecord = exerciseRecordRepository.findById(savedId).orElseThrow();
        assertThat(deletedRecord).isNull();
    }

    /**
     * 운동 기록 조회
     */
    @Test
    void 운동_기록_조회() {
        // given
        ExerciseRecordRequest request1 = new ExerciseRecordRequest();
        request1.setExerciseName("근력1");
        request1.setDurationMinutes(30);
        request1.setCaloriesBurned(300);
        request1.setExerciseType(ExerciseType.CARDIO);

        ExerciseRecordRequest request2 = new ExerciseRecordRequest();
        request2.setExerciseName("근력2");
        request2.setDurationMinutes(45);
        request2.setCaloriesBurned(400);
        request2.setExerciseType(ExerciseType.WEIGHT);

        Long savedId1 = recordService.saveExerciseRecord(request1, savedMemberId);
        Long savedId2 = recordService.saveExerciseRecord(request2, savedMemberId);

        // when
        List<ExerciseRecord> records = recordService.getExerciseRecords(savedMemberId);

        // then
        assertThat(records).hasSize(2);
        assertThat(records)
                .extracting(record -> record.getExerciseType())
                .containsExactlyInAnyOrder(ExerciseType.CARDIO, ExerciseType.WEIGHT);
    }
}
