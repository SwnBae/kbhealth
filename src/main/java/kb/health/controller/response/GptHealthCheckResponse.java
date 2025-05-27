package kb.health.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GptHealthCheckResponse {

    private String healthStatus;          // 전반적인 건강상태 (예: "양호", "주의", "개선필요")
    private String nutritionAnalysis;     // 영양상태 분석
    private String recommendations;       // 개선 권장사항
    private String warningMessage;        // 주의사항 (있을 경우)
}