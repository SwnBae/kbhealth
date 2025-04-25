package kb.health.record;

import kb.health.Repository.RecordRepository;
import kb.health.Service.*;
import kb.health.domain.Member;
import kb.health.domain.record.*;
import kb.health.domain.request.ExerciseRecordRequest;
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
    RecordRepository recordRepository;

    Long savedMemberId;

    @BeforeEach
    void setUp() {
        Member member = Member.create("account", "Test", "password", "010-0000-0000");
        savedMemberId = memberService.save(member);
    }

    /**
     * 운동 기록 삽입
     */
    @Test
    void 운동_기록_삽입() {
        // given
        ExerciseRecordRequest request = new ExerciseRecordRequest(30, 300, ExerciseType.CARDIO);

        // when
        Long savedId = recordService.saveExerciseRecord(request, savedMemberId);
        ExerciseRecord savedRecord = recordRepository.findExerciseRecordById(savedId);

        // then
        assertThat(savedRecord).isNotNull();
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
        ExerciseRecordRequest request = new ExerciseRecordRequest(30, 300, ExerciseType.CARDIO);

        Long savedId = recordService.saveExerciseRecord(request, savedMemberId);
        ExerciseRecord savedRecord = recordRepository.findExerciseRecordById(savedId);

        // when: 운동시간, 칼로리, 운동 타입 수정
        ExerciseRecordRequest updateRequest = new ExerciseRecordRequest();
        updateRequest.setDurationMinutes(45);
        updateRequest.setCaloriesBurned(400);
        updateRequest.setExerciseType(ExerciseType.WEIGHT);

        recordService.updateExerciseRecord(savedRecord.getId(), updateRequest);

        // then
        ExerciseRecord updatedRecord = recordService.getExerciseRecord(savedId);
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
        ExerciseRecordRequest request = new ExerciseRecordRequest(30, 300, ExerciseType.CARDIO);

        Long savedId = recordService.saveExerciseRecord(request, savedMemberId);

        // when
        recordService.deleteExerciseRecord(savedId);

        // then
        ExerciseRecord deletedRecord = recordRepository.findExerciseRecordById(savedId);
        assertThat(deletedRecord).isNull();
    }

    /**
     * 운동 기록 조회
     */
    @Test
    void 운동_기록_조회() {
        // given
        ExerciseRecordRequest request1 = new ExerciseRecordRequest();
        request1.setDurationMinutes(30);
        request1.setCaloriesBurned(300);
        request1.setExerciseType(ExerciseType.CARDIO);

        ExerciseRecordRequest request2 = new ExerciseRecordRequest();
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
