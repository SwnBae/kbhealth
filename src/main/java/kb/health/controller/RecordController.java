package kb.health.controller;

import kb.health.service.RecordService;
import kb.health.authentication.CurrentMember;
import kb.health.authentication.LoginMember;
import kb.health.controller.request.DietRecordRequest;
import kb.health.controller.request.ExerciseRecordRequest;
import kb.health.controller.response.ExerciseRecordResponse;
import kb.health.domain.record.DietRecord;
import kb.health.controller.response.DietRecordResponse;
import kb.health.domain.record.ExerciseRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<DietRecordResponse>> getDietRecordList(@LoginMember CurrentMember currentMember) {
        List<DietRecord> records = recordService.getDietRecords(currentMember.getId());
        List<DietRecordResponse> response = records.stream()
                .map(DietRecordResponse::create)
                .collect(Collectors.toList());

        for(DietRecordResponse dietRecordResponse : response) {
            System.out.println(dietRecordResponse);
        }
        return ResponseEntity.ok(response); // 200 OK와 함께 응답
    }

    // 기록 생성
    @PostMapping("/diet")
    public ResponseEntity<String> createDietRecord(@LoginMember CurrentMember currentMember, @RequestBody DietRecordRequest request) {
        recordService.saveDietRecord(request, currentMember.getId());
        return ResponseEntity.ok("식단 추가 성공"); // 성공 메시지와 함께 응답
    }

    // 특정 기록 조회
    @GetMapping("/diet/{drId}")
    public ResponseEntity<DietRecordResponse> getDietRecord(@LoginMember CurrentMember currentMember, @PathVariable Long drId) {
        DietRecord dietRecord = recordService.getDietRecord(drId);
        return ResponseEntity.ok(DietRecordResponse.create(dietRecord)); // 200 OK와 함께 응답
    }

    // 기록 수정
    @PutMapping("/diet/{drId}")
    public ResponseEntity<String> updateDietRecord(@LoginMember CurrentMember currentMember, @PathVariable Long drId, @RequestBody DietRecordRequest request) {
        recordService.updateDietRecord(currentMember.getId(), drId, request);
        return ResponseEntity.ok("식단 수정 성공"); // 수정 성공 메시지
    }

    // 기록 삭제
    @DeleteMapping("/diet/{drId}")
    public ResponseEntity<String> deleteDietRecord(@LoginMember CurrentMember currentMember, @PathVariable Long drId) {
        recordService.deleteDietRecord(currentMember.getId(), drId);
        return ResponseEntity.ok("식단 삭제 성공"); // 삭제 성공 메시지
    }

    /**
     * 운동
     */

    // 기록 목록
    @GetMapping("/exercise")
    public ResponseEntity<List<ExerciseRecordResponse>> getExerciseRecordList(@LoginMember CurrentMember currentMember) {
        List<ExerciseRecord> records = recordService.getExerciseRecords(currentMember.getId());
        List<ExerciseRecordResponse> response = records.stream()
                .map(ExerciseRecordResponse::create)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response); // 200 OK와 함께 응답
    }

    // 기록 생성
    @PostMapping("/exercise")
    public ResponseEntity<String> createExerciseRecord(@LoginMember CurrentMember currentMember, @RequestBody ExerciseRecordRequest request) {
        recordService.saveExerciseRecord(request, currentMember.getId());
        return ResponseEntity.ok("운동 추가 성공"); // 성공 메시지와 함께 응답
    }

    // 특정 기록 조회
    @GetMapping("/exercise/{exId}")
    public ResponseEntity<ExerciseRecordResponse> getExerciseRecord(@LoginMember CurrentMember currentMember, @PathVariable Long exId) {
        ExerciseRecord exerciseRecord = recordService.getExerciseRecord(exId);
        return ResponseEntity.ok(ExerciseRecordResponse.create(exerciseRecord)); // 200 OK와 함께 응답
    }

    // 기록 수정
    @PutMapping("/exercise/{exId}")
    public ResponseEntity<String> updateExerciseRecord(@LoginMember CurrentMember currentMember, @PathVariable Long exId, @RequestBody ExerciseRecordRequest request) {
        recordService.updateExerciseRecord(currentMember.getId(), exId, request);
        return ResponseEntity.ok("운동 수정 성공"); // 수정 성공 메시지
    }

    // 기록 삭제
    @DeleteMapping("/exercise/{exId}")
    public ResponseEntity<String> deleteExerciseRecord(@LoginMember CurrentMember currentMember, @PathVariable Long exId) {
        recordService.deleteExerciseRecord(currentMember.getId(), exId);
        return ResponseEntity.ok("운동 삭제 성공"); // 삭제 성공 메시지
    }
}
