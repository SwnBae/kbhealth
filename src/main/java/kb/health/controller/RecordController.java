package kb.health.controller;

import kb.health.Service.RecordService;
import kb.health.authentication.CurrentMember;
import kb.health.authentication.LoginMember;
import kb.health.controller.request.DietRecordRequest;
import kb.health.domain.record.DietRecord;
import kb.health.controller.response.DietRecordResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dietRecords")
public class RecordController {

    private final RecordService recordService;

    // 다이어트 기록 목록
    @GetMapping
    public List<DietRecordResponse> list(@LoginMember CurrentMember currentMember) {
        return recordService.getDietRecords(currentMember.getId()); // 로그인된 사용자만 조회하도록 처리 가능
    }

    // 다이어트 기록 생성
    @PostMapping
    public void create(@LoginMember CurrentMember currentMember, @RequestBody DietRecordRequest request) {
        recordService.saveDietRecord(request, currentMember.getId());
    }

    // 특정 다이어트 기록 조회
    @GetMapping("/{drId}")
    public DietRecord getDietRecord(@LoginMember CurrentMember currentMember, @PathVariable Long drId) {
        return recordService.getDietRecord(drId);
    }

    // 다이어트 기록 수정
    @PutMapping("/{drId}")
    public void update(@LoginMember CurrentMember currentMember, @PathVariable Long drId, @RequestBody DietRecordRequest request) {
        recordService.updateDietRecord(currentMember.getId(), drId, request);
    }

    // 다이어트 기록 삭제
    @DeleteMapping("/{drId}")
    public void delete(@LoginMember CurrentMember currentMember, @PathVariable Long drId) {
        recordService.deleteDietRecord(currentMember.getId(), drId);
    }
}