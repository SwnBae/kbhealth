package kb.health.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class DietRequest {
    private String menu;
    private int calories;

    public DietRequest(String menu, int calories) {
        this.menu = menu;
        this.calories = calories;
    }
}

