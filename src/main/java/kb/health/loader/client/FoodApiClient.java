package kb.health.loader.client;

import kb.health.loader.dto.FoodItem;
import kb.health.loader.dto.FoodResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FoodApiClient {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://api.data.go.kr/openapi/tn_pubr_public_nutri_food_info_api")
            .build();

    @Value("${spring.api.food.service-key}")
    private String serviceKey;

    public List<FoodItem> getAllFoods() {
        int pageSize = 100;
        int page = 1;

        // 첫 페이지에서 totalCount 가져옴
        FoodResponse first = getFoodPage(page, pageSize);

        if (first == null) {
            System.out.println("first가 null입니다.");
            return List.of();
        }

        if (first.response() == null) {
            System.out.println("response가 null입니다.");
            return List.of();
        }

        if (first.response().body() == null) {
            System.out.println("body가 null입니다.");
            return List.of();
        }

        if (first.response().body().items() == null) {
            System.out.println("items가 null입니다.");
            return List.of();
        }


        System.out.println(first.response().body().totalCount());

        List<FoodItem> all = new ArrayList<>(first.response().body().items());
        int total = Integer.parseInt(first.response().body().totalCount());
        int totalPages = (int) Math.ceil((double) total / pageSize);

        // 이후 페이지들 가져오기 (1페이지를 제외한 나머지)
        for (int i = 2; i <= 5; i++) {
            FoodResponse pageData = getFoodPage(i, pageSize);
            if (pageData != null && pageData.response() != null && pageData.response().body() != null && pageData.response().body().items() != null) {
                all.addAll(pageData.response().body().items());
            }

            try {
                Thread.sleep(3000); // 3초 동안 대기
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return all;
    }

    private FoodResponse getFoodPage(int page, int pageSize) {
        URI uri = URI.create(String.format(
                "http://api.data.go.kr/openapi/tn_pubr_public_nutri_food_info_api?serviceKey=%s&pageNo=%d&numOfRows=%d&type=json",
                serviceKey, page, pageSize
        ));

        System.out.println("요청 URI: " + uri);  // 요청 주소 출력

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(FoodResponse.class)
                .block();
    }


}
