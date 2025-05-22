package kb.health.loader.dto;

public record FoodItem(
        String foodNm,         // 메뉴 이름
        String foodLv3Nm,      // 대분류
        String nutConSrtrQua,  // 기준량 (예: 100g, 100ml)
        String enerc,          // 에너지(kcal)
        String prot,           // 단백질
        String fatce,          // 지방
        String chocdf,         // 탄수화물
        String sugar,          // 당류
        String fibtg,          // 식이섬유
        String nat             // 나트륨
) {
}

