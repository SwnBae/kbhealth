package kb.health.controller;

import kb.health.loader.service.DietLoadService;
import kb.health.service.RecordService;
import kb.health.authentication.CurrentMember;
import kb.health.authentication.LoginMember;
import kb.health.controller.request.DietRequest;
import kb.health.domain.record.Diet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemController {

    private final RecordService recordService;
    private final DietLoadService dietLoadService;

    @GetMapping
    public ResponseEntity<List<Diet>> getAllItems(@LoginMember CurrentMember currentMember) {
        return ResponseEntity.ok(recordService.getDietList());
    }

    @PostMapping
    public ResponseEntity<?> createItem(@LoginMember CurrentMember currentMember, @RequestBody DietRequest dietRequest) {
        recordService.addDiet(dietRequest);
        return ResponseEntity.ok("식단 추가 성공");
    }

    @GetMapping("/{dietId}")
    public ResponseEntity<?> getItemById(@LoginMember CurrentMember currentMember, @PathVariable Long dietId) {
        return ResponseEntity.ok(recordService.getDiet(dietId));
    }

    @PutMapping("/{dietId}")
    public ResponseEntity<?> update(@LoginMember CurrentMember currentMember, @PathVariable Long dietId, @RequestBody DietRequest dietRequest) {
        recordService.updateDiet(dietId, dietRequest);
        return ResponseEntity.ok("음식 수정 성공");
    }

    @DeleteMapping("/{dietId}")
    public ResponseEntity<?> delete(@LoginMember CurrentMember currentMember, @PathVariable Long dietId) {
        recordService.deleteDiet(dietId);
        return ResponseEntity.ok("음식 삭제 성공");
    }

    // 검색
    @GetMapping("/search")
    public ResponseEntity<List<Diet>> searchItemsByMenu(@RequestParam String menu) {
        List<Diet> diets = recordService.searchDietsByMenu(menu);
        return ResponseEntity.ok(diets);
    }

    // GET으로도 접근 가능하게 추가
    @GetMapping("/load-data")
    public ResponseEntity<?> loadDietDataGet(@LoginMember CurrentMember currentMember) {
        try {
            int count = dietLoadService.saveAllFromApi();
            return ResponseEntity.ok("음식 데이터 로드 완료. 총 저장된 식품 수: " + count);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("음식 데이터 로드 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
