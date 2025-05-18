package kb.health.client;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class GptClient {

    private WebClient webClient;

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    @Value("${openai.url}")
    private String apiUrl;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl.replaceAll("/chat/completions$", "")) // https://api.openai.com/v1
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }
    public String askGpt(String prompt) {
        // OpenAI API 요청 Body를 Map으로 생성
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", new Object[] {
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                }
        );

        return webClient.post()
                .uri("/chat/completions")  // ✅ 여기만 고치면 됨
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    var choices = (java.util.List<?>) response.get("choices");
                    if (choices == null || choices.isEmpty()) {
                        return "추천 결과가 없습니다.";
                    }
                    Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
                    Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
                    return message.get("content").toString();
                })
                .block();

    }
}
