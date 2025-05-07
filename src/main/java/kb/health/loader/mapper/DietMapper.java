package kb.health.loader.mapper;

import kb.health.domain.record.Diet;
import kb.health.loader.dto.FoodItem;

public class DietMapper {

    public static Diet toEntity(FoodItem item) {
        Diet diet = new Diet();
        diet.setMenu(item.foodNm());
        diet.setCategory(item.foodLv3Nm());
        diet.setStandardAmount(parseDouble(item.nutConSrtrQua()));
        diet.setCalories(parseInt(item.enerc()));
        diet.setProtein(parseDouble(item.prot()));
        diet.setFat(parseDouble(item.fatce()));
        diet.setCarbohydrates(parseDouble(item.chocdf()));
        diet.setSugars(parseDouble(item.sugar()));
        diet.setFiber(parseDouble(item.fibtg()));
        diet.setSodium(parseDouble(item.nat()));
        return diet;
    }

    private static double parseDouble(String value) {
        try {
            return Double.parseDouble(extractNumber(value));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static String extractNumber(String value) {
        if (value == null) return "0";
        return value.replaceAll("[^\\d.]+", "");
    }


    private static int parseInt(String value) {
        try {
            return (int) Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }
}

