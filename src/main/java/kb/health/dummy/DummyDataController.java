package kb.health.dummy;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dummy")
public class DummyDataController {

    private final DummyDataService dummyDataService;

    /**
     * 무작위 더미 데이터 생성 API
     * - 회원, 팔로우 관계, 식단/운동 기록, 피드 데이터를 모두 생성
     */
    @GetMapping("/generate")
    public ResponseEntity<Map<String, Object>> generateDummyData() {
        try {
            Map<String, Object> result = dummyDataService.generateRandomData();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "더미 데이터 생성 중 오류 발생: " + e.getMessage(),
                    "error", e.getClass().getSimpleName()
            ));
        }
    }

    /**
     * 모든 기존 데이터에 대한 점수, 랭킹, 영양소 달성률 계산 API
     */
    @GetMapping("/calculate/all")
    public ResponseEntity<Map<String, Object>> calculateAllData() {
        try {
            Map<String, Object> result = dummyDataService.calculateAllExistingData();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "전체 데이터 계산 중 오류 발생: " + e.getMessage(),
                    "error", e.getClass().getSimpleName()
            ));
        }
    }

    /**
     * 최근 30일 데이터에 대한 점수, 랭킹, 영양소 달성률 계산 API
     */
    @GetMapping("/calculate/recent")
    public ResponseEntity<Map<String, Object>> calculateRecentData() {
        try {
            Map<String, Object> result = dummyDataService.calculateLast30DaysData();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "최근 30일 데이터 계산 중 오류 발생: " + e.getMessage(),
                    "error", e.getClass().getSimpleName()
            ));
        }
    }

    /**
     * 시스템 상태 확인 API
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        Map<String, Object> status = dummyDataService.getSystemStatus();
        return ResponseEntity.ok(status);
    }
}