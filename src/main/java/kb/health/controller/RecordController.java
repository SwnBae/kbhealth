package kb.health.controller;

import kb.health.Service.RecordService;
import kb.health.authentication.CurrentMember;
import kb.health.authentication.LoginMember;
import kb.health.controller.request.DietRecordRequest;
import kb.health.controller.request.ExerciseRecordRequest;
import kb.health.controller.response.ExerciseRecordResponse;
import kb.health.domain.record.DietRecord;
import kb.health.controller.response.DietRecordResponse;
import kb.health.domain.record.ExerciseRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;

    /**
     * 식단
     */

    // 기록 목록
    @GetMapping("/diet")
    public List<DietRecordResponse> getDietRecordList(@LoginMember CurrentMember currentMember) {
        List<DietRecord> records = recordService.getDietRecords(currentMember.getId());
        return records.stream()
                .map(DietRecordResponse::create)
                .collect(Collectors.toList());
    }

    // 기록 생성
    @PostMapping("/diet")
    public void createDietRecord(@LoginMember CurrentMember currentMember, @RequestBody DietRecordRequest request) {
        recordService.saveDietRecord(request, currentMember.getId());
    }

    // 특정 기록 조회
    @GetMapping("/diet/{drId}")
    public DietRecordResponse getDietRecord(@LoginMember CurrentMember currentMember, @PathVariable Long drId) {
        DietRecord dietRecord = recordService.getDietRecord(drId);
        return DietRecordResponse.create(dietRecord);
    }

    // 기록 수정
    @PutMapping("/diet/{drId}")
    public void updateDietRecord(@LoginMember CurrentMember currentMember, @PathVariable Long drId, @RequestBody DietRecordRequest request) {
        recordService.updateDietRecord(currentMember.getId(), drId, request);
    }

    // 기록 삭제
    @DeleteMapping("/diet/{drId}")
    public void deleteDietRecord(@LoginMember CurrentMember currentMember, @PathVariable Long drId) {
        recordService.deleteDietRecord(currentMember.getId(), drId);
    }

    /**
     * 운동
     */

    // 기록 목록
    @GetMapping("/exercise")
    public List<ExerciseRecordResponse> getExerciseRecordList(@LoginMember CurrentMember currentMember) {
        List<ExerciseRecord> records = recordService.getExerciseRecords(currentMember.getId());
        return records.stream()
                .map(ExerciseRecordResponse::create)
                .collect(Collectors.toList());
    }

    // 기록 생성
    @PostMapping("/exercise")
    public void createExerciseRecord(@LoginMember CurrentMember currentMember, @RequestBody ExerciseRecordRequest request) {
        recordService.saveExerciseRecord(request, currentMember.getId());
    }

    // 특정 기록 조회
    @GetMapping("/exercise/{exId}")
    public ExerciseRecordResponse getExerciseRecord(@LoginMember CurrentMember currentMember, @PathVariable Long exId) {
        ExerciseRecord exerciseRecord = recordService.getExerciseRecord(exId);
        return ExerciseRecordResponse.create(exerciseRecord);
    }

    // 기록 수정
    @PutMapping("/exercise/{exId}")
    public void updateExerciseRecord(@LoginMember CurrentMember currentMember, @PathVariable Long exId, @RequestBody ExerciseRecordRequest request) {
        recordService.updateExerciseRecord(currentMember.getId(), exId, request);
    }

    // 기록 삭제
    @DeleteMapping("/exercise/{exId}")
    public void deleteExerciseRecord(@LoginMember CurrentMember currentMember, @PathVariable Long exId) {
        recordService.deleteExerciseRecord(currentMember.getId(), exId);
    }
}