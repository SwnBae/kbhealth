package kb.health.domain.record;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Diet {

    @Id @GeneratedValue
    @Column(name = "diet_id")
    private Long id;

    @Column(length = 50, nullable = false)
    private String menu;

    @Column(nullable = false)
    private int calories;

    private String category;

    private double standardAmount;  // 예: 100g

    private double protein;         // 예: 6.7
    private double fat;             // 예: 5.16
    private double carbohydrates;   // 예: 15.94
    private double sugars;          // 예: 0.16
    private double fiber;           // 예: 0.7
    private double sodium;          // 예: 181
}
